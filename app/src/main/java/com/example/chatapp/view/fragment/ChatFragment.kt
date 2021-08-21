package com.example.chatapp.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.adapter.MessageListAdapter
import com.example.chatapp.helper.*
import com.example.chatapp.model.network.APIConstants
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.friend_chat.User
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.ChatUserViewModel
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.example.chatapp.viewmodel.UploadChatImageViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_contacts_list.*
import kotlinx.android.synthetic.main.fragment_friends_chat_image_preview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val USER_ID = "userId"

class ChatFragment : Fragment(), MessageListAdapter.ChatDeleteClickListener, UploadImageListener {
    private var mSocket: Socket? = null
    lateinit var accessToken: String
    lateinit var userId: String
    lateinit var friendsPhoneno: String
    private val chatViewModel: FriendChatViewModel by viewModel()
    private val chatUserViewModel: ChatUserViewModel by viewModel()
    private val uploadChatImageViewModel: UploadChatImageViewModel by viewModel()

    private lateinit var mMessageAdapter: MessageListAdapter
    private var size = 0
    private var chatUserSize = 0
    private var page = 1
    private var rowPerPage = 10

    private var image: String? = null
    private var imageUri: Uri? = null
    private val pickImage = 100
    private val TAKE_PICTURE = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID).toString()
            friendsPhoneno = it.getString("phoneno").toString()
        }

        Log.i("arguments","phone no => ${friendsPhoneno} user id => ${userId}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        mMessageAdapter = MessageListAdapter(mutableListOf(), requireActivity().supportFragmentManager,this@ChatFragment)
        chat_recycler.apply {
            adapter = mMessageAdapter
        }

        initSocket()

        PushDownAnim.setPushDownAnimTo(cameraBtn).setOnClickListener {

            selectImage()
        }

        PushDownAnim.setPushDownAnimTo(chat_btnSend).setOnClickListener {
            sendMessage()
        }

        CoroutineScope(Dispatchers.IO).launch {
            size = chatViewModel.getMessageCount(userId!!)
            chatUserSize = chatUserViewModel.isChatUserAvailable(userId!!)
            withContext(Dispatchers.Main) {
                getChatMessage()
            }
        }

    }

    private fun initSocket() {
        while (mSocket == null) {
            mSocket = SocketHelper.loginSocket(requireContext())
        }
        //listeners
        mSocket?.let { socket ->
            socket.on("chat message", chatMessageListener)
            socket.on("ackStatus", ackStatusListener)

        }

        mSocket?.connect()
        Log.d("Socket_connected", "Socket_connected => ${mSocket?.connected()}")
    }


    private val chatMessageListener = Emitter.Listener {
        requireActivity().runOnUiThread {
            Log.i("checkList","reached here")

            val data = it[0] as JSONObject
            try {
                val messageData = data.getJSONObject("data")
                val message = Gson().fromJson(messageData.toString(), Message::class.java)
                if (message.sentBy.id == userId) {

                    Log.i("data","data => ${message}")

                    val messages = com.eduaid.child.models.pojo.friend_chat.Message(
                        message.msgUuid,
                        message.msg,
                        message.image,
                        com.example.chatapp.model.pojo.friend_chat.User(message.sentBy.id,message.sentBy.phoneno),
                        message.sentOn,
                    )
                    messages.isSender = false
                    if(message.image != ""){
                        messages.messageType = "image"
                        message.messageType = "image"
                        mMessageAdapter.addMessage(message)

                    }else{
                        messages.messageType = "text"
                        message.messageType = "text"
                        mMessageAdapter.addMessage(message)

                    }
                    Log.i("imageType","imagetype => ${message.image+","+message.messageType}")
                    saveMessage(messages)
                    sendAckMessage(message.msgUuid, userId, true)

                    /*chat user save*/
                    CoroutineScope(Dispatchers.IO).launch {
                        chatUserSize = chatUserViewModel.isChatUserAvailable(message.sentBy.id)
                        Log.i("chatUserSize", "chat use size => ${chatUserSize}")
                        withContext(Dispatchers.Main) {

                            val chatUser = ChatUser(
                                message.sentBy.id,
                                message.sentBy.phoneno,
                                message.msg,
                                "",
                                message.sentOn
                            )

                            if(chatUserSize == 1){
                                updateChatUser(chatUser)
                            }else{
                                saveChatUser(chatUser)
                            }

                        }

                    }

                }
            } catch (e: JSONException) {
                Log.d("Socket_connected","exception -> ${e.message}")
                e.printStackTrace()
            }
        }
    }


    private val ackStatusListener = Emitter.Listener {


        requireActivity().runOnUiThread {
            Log.i("checkList","reached here too")
            val data = it[0] as JSONObject
            try {
                val status = data.getInt("status")
                val msgUuid = data.getString("msgUuid")
                Log.i("msgAckstatus", "status => ${data}")

                if(status == 2){

                    chatViewModel.updateIsSeen(true, msgUuid)
                    mMessageAdapter.updateSeen(msgUuid)

                }else if (status == 0) {

                    chatViewModel.updateIsSent(true, msgUuid)
                    mMessageAdapter.updateSent(msgUuid)

                }


            } catch (e: JSONException) {
                Log.d("ACK","ACK ---> $data")
                e.printStackTrace()
            }
        }
    }


    private fun sendMessage() {
        val messageText = textInput.text.toString().trim()
        if (messageText.isEmpty()) {
            textInput.error = "message cannot be empty"
            return
        }
        //val id = (0..1).random()
        val currentThreadTimeMillis = System.currentTimeMillis()
        val msgUuid = currentThreadTimeMillis.toString()

        val message = com.eduaid.child.models.pojo.friend_chat.Message(
            msgUuid,
            messageText,
            "",
            com.example.chatapp.model.pojo.friend_chat.User(userId,friendsPhoneno),
            currentThreadTimeMillis,
        )
        message.isSender = true
        message.messageType = "text"

        mMessageAdapter.addMessage(message)
        saveMessage(message)

        /*chat user save*/
        val chatUser = ChatUser(
            userId,
            friendsPhoneno,
            messageText,
            "",
            currentThreadTimeMillis
        )
        if(chatUserSize == 1){
            updateChatUser(chatUser)
        }else{
            saveChatUser(chatUser)
        }

        Log.d("emitting send message","emitting send message")
        emitMessage(
            msgUuid,
            messageText,
            currentThreadTimeMillis,
            "",
            userId
        )
        textInput.setText("")
        requireActivity().hideSoftKeyboard()
    }

    private fun emitMessage(
        msgUuid: String,
        messageText: String,
        currentThreadTimeMillis: Long,
        image: String? = null,
        userId: String
    ) {
        val jsonMessage = JSONObject()
        jsonMessage.put("msgUuid", msgUuid)
        jsonMessage.put("msg", messageText)
        if (image != null)
            jsonMessage.put("image", image)
        jsonMessage.put("sentOn", currentThreadTimeMillis)
        jsonMessage.put("userId", userId)

        Log.d("Emit","emit started")
        mSocket?.emit("chat message", jsonMessage.toString(), Ack {
            val isBlocked = it[0]
            Log.d("blocked","blocked = $isBlocked")
        })
    }

    private fun sendAckMessage(msgUuid: String, senderId: String, hasRead: Boolean) {
        val ackObj = JSONObject()
        ackObj.put("msgUuid", msgUuid)
        ackObj.put("senderId", senderId)
        ackObj.put("hasRead", hasRead)
        mSocket?.emit("message ack", ackObj.toString())
    }

    private fun saveMessage(message: Message) {
        Log.i("saveMessage","reached here...")
        chat_recycler.scrollToPosition(mMessageAdapter.itemCount - 1)
        chatViewModel.saveMessage(message)
    }

    private fun getChatMessage(){
        chatViewModel.getChatMessages(userId).observe(requireActivity(), { messages ->
            Log.d("msgSize","msg list size = ${messages.size}")
            if (!messages.isNullOrEmpty()) {

                messages.forEach { message ->
                    if (!message.hasRead) {
                        message.hasRead = true
                        sendAckMessage(message.msgUuid, userId!!, true)
                        //update hasRead in db
                        chatViewModel.updateMessage(message)
                    }
                }

                mMessageAdapter.addAllMessages(messages)


            } else {
                Log.d("msgSize","msg list size => ${messages.size}")
            }
        })
    }


    private fun saveChatUser(chatUser: ChatUser) {
        Log.i("saveChatUser","reached here...")
        chatUserViewModel.saveChatUser(chatUser)
    }

    private fun updateChatUser(chatUser: ChatUser) {
        chatUserViewModel.updateChatUser(chatUser)
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    dispatchCameraIntent()
                }
                "Choose from Gallery" -> {
                    dispatchGalleryIntent()
                }
                /*"Upload Pdf" -> {
                    dispatchPdfIntent()
                }*/
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }

    private fun dispatchCameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImage()

            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                imageUri =
                    FileProvider.getUriForFile(requireActivity(), APIConstants.FILE_PROVIDER, photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, TAKE_PICTURE)
            }
        }
    }

    private fun dispatchGalleryIntent() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    private fun createImage(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageName = "JPEG_" + timeStamp + "_"
        var storeDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var tempImage = File.createTempFile(imageName, ".jpg", storeDir)
        image = tempImage.absolutePath
        return tempImage

    }

    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = requireActivity().contentResolver.query(contentUri!!, proj, null, null, null)
            assert(cursor != null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun showImageDialog(absolutePath: String) {
        val bundle = Bundle()
        bundle.putString("path", absolutePath)
        val dialogFragment = FriendsChatImagePreviewFragment(this)
        dialogFragment.arguments = bundle
        dialogFragment.show(requireActivity().supportFragmentManager, "signature")
    }

    override fun onDeleteClicked(view: View, position: Int) {
        val message = view.tag as Message
        chatViewModel.deleteChat(message)
        mMessageAdapter.removeMessage(message)

        chatViewModel.getChatMessages(userId).observe(requireActivity(), { messages ->

            if (!messages.isNullOrEmpty()) {
                val chatUser = ChatUser(
                    messages.last().sentBy.id,
                    messages.last().sentBy.phoneno,
                    messages.last().msg,
                    "",
                    messages.last().sentOn
                )
                updateChatUser(chatUser)

            } else {
                Log.d("msgSize","msg list size => ${messages.size}")
            }
        })

    }

    private fun getUniqueUuid(): String {
        return userId!! + System.currentTimeMillis().toString()
    }

    private fun uploadFile(
        receiver_id: String,
        messageType: String,
        imagePart: MultipartBody.Part,
        captionMessage: String = ""
    ) {
        val loader = requireActivity().loadingDialog()
        loader.show()
        uploadChatImageViewModel.uploadImage(receiver_id,"image",imagePart,accessToken).observe(viewLifecycleOwner,{ outcome->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){

                        val imageUrl = APIConstants.BASE_URL+"/images/"+outcome.data.files[0].filename

                        Toast.makeText(activity,"success !!!", Toast.LENGTH_SHORT).show()

                        val msgUuid = getUniqueUuid()
                        val currentThreadTimeMillis = System.currentTimeMillis()
                        val message = Message(
                            msgUuid,
                            captionMessage,
                            imageUrl,
                            User(userId, friendsPhoneno),
                            currentThreadTimeMillis
                        )
                        message.isSender = true
                        message.messageType = "image"

                        mMessageAdapter.addMessage(message)
                        saveMessage(message)

                        /*chat user save*/
                        val chatUser = ChatUser(
                            userId,
                            friendsPhoneno,
                            captionMessage,
                            "",
                            currentThreadTimeMillis
                        )
                        if(chatUserSize == 1){
                            updateChatUser(chatUser)
                        }else{
                            saveChatUser(chatUser)
                        }

                        Log.d("emitting send message","emitting send message")
                        emitMessage(
                            msgUuid,
                            captionMessage,
                            currentThreadTimeMillis,
                            imageUrl,
                            userId
                        )

                    }else{
                        Toast.makeText(activity,"error !!!", Toast.LENGTH_SHORT).show()
                    }
                }

                is Outcome.Failure<*> -> {
                    Toast.makeText(activity,outcome.e.message, Toast.LENGTH_SHORT).show()
                    Log.i("statusMsg",outcome.e.message.toString())

                    outcome.e.printStackTrace()
                    Log.i("status",outcome.e.cause.toString())
                }
            }
        })
    }


    override fun uploadImage(path: String, message: String) {
        try {

            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {


                    val imagePart = requireActivity().createMultiPart("image", path)
                    val messageType = "image"
                    Log.i("imagePart: " ,imagePart.toString())

                    uploadFile(userId, messageType, imagePart, message)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == pickImage) {

        try {
                imageUri = data?.data
                val path = getRealPathFromUri(imageUri)
                val imageFile = File(path!!)
                showImageDialog(imageFile.absolutePath)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (requestCode == TAKE_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {

            try {

                showImageDialog(image.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("socketOff","Done")
        mSocket?.let { socket ->
            socket.off("chat message", chatMessageListener)
            socket.off("ackStatus", ackStatusListener)
        }
        mSocket?.disconnect()
    }
}