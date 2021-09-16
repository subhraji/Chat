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
import androidx.navigation.fragment.findNavController
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
import id.zelory.compressor.Compressor
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
    lateinit var friendsAvatar: String


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
    private val PDF_REQUEST_CODE = 101



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID).toString()
            friendsPhoneno = it.getString("phoneno").toString()
            friendsAvatar = it.getString("avatar").toString()
        }

        Log.i("friendsAvatar",friendsAvatar)
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

        PushDownAnim.setPushDownAnimTo(chat_frg_back_arrow).setOnClickListener {
            findNavController().navigateUp()
        }

        chat_frg_phone_no_txt.text = friendsPhoneno

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
            val data = it[0] as JSONObject
            Log.i("data","data => ${data}")
            try {
                val messageData = data.getJSONObject("data")
                val message = Gson().fromJson(messageData.toString(), Message::class.java)
                if (message.sentBy.id == userId) {

                    Log.i("data","msg data => ${message}")
                    Log.i("fileName","msg type 1 => ${message.fileName}")
                    val msgContent = if (message.messageType == "pdf") {
                        "file"
                    } else {
                        message.msg
                    }

                    val messagesList = com.eduaid.child.models.pojo.friend_chat.Message(
                        message.msgUuid,
                        msgContent,
                        message.image,
                        com.example.chatapp.model.pojo.friend_chat.User(message.sentBy.id,message.sentBy.phoneno,message.sentBy.avatar),
                        message.sentOn,
                    )
                    messagesList.isSender = false
                    messagesList.messageType = message.messageType
                    messagesList.fileName = message.fileName
                    mMessageAdapter.addMessage(message)

                    saveMessage(messagesList)
                    sendAckMessage(message.msgUuid, userId, true)

                    /*chat user save*/
                    CoroutineScope(Dispatchers.IO).launch {
                        chatUserSize = chatUserViewModel.isChatUserAvailable(message.sentBy.id)
                        Log.i("chatUserSize", "chat use size => ${chatUserSize}")
                        withContext(Dispatchers.Main) {

                            val chatUser = ChatUser(
                                message.sentBy.id,
                                message.sentBy.phoneno,
                                msgContent,
                                message.sentBy.avatar,
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
            com.example.chatapp.model.pojo.friend_chat.User(userId,friendsPhoneno,friendsAvatar),
            currentThreadTimeMillis,
        )
        message.isSender = true
        message.messageType = "text"
        message.isSent = true

        mMessageAdapter.addMessage(message)
        saveMessage(message)

        /*chat user save*/
        val chatUser = ChatUser(
            userId,
            friendsPhoneno,
            messageText,
            friendsAvatar,
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
            userId,
            "text"
        )
        textInput.setText("")
        requireActivity().hideSoftKeyboard()
    }

    private fun emitMessage(
        msgUuid: String,
        messageText: String,
        currentThreadTimeMillis: Long,
        image: String? = null,
        userId: String,
        messageType: String = "text",
        fileName: String? = null
    ) {
        val jsonMessage = JSONObject()
        jsonMessage.put("msgUuid", msgUuid)
        jsonMessage.put("msg", messageText)
        jsonMessage.put("messageType", messageType)
        if (image != null)
            jsonMessage.put("image", image)
            jsonMessage.put("fileName", fileName)
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
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Choose Documents", "Cancel")
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
                "Choose Documents" -> {
                    dispatchPdfIntent()
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }

    private fun dispatchPdfIntent() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, PDF_REQUEST_CODE)
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
        Log.i("part","part => ${imagePart}")
        val loader = requireActivity().loadingDialog()
        loader.show()
        uploadChatImageViewModel.uploadImage(receiver_id,messageType,imagePart,accessToken).observe(viewLifecycleOwner,{ outcome->
            loader.dismiss()
            when(outcome){
                is Outcome.Success ->{
                    if(outcome.data.status =="success"){


                        val messageContent = if (messageType == "pdf") {
                            outcome.data.files[0].fieldname
                        } else {
                            captionMessage
                        }

                        val imageUrl = APIConstants.BASE_URL+"/images/"+outcome.data.files[0].filename

                        val msgUuid = getUniqueUuid()
                        val currentThreadTimeMillis = System.currentTimeMillis()
                        val message = Message(
                            msgUuid,
                            messageContent,
                            imageUrl,
                            User(userId, friendsPhoneno,friendsAvatar),
                            currentThreadTimeMillis
                        )
                        message.isSender = true
                        message.messageType = messageType
                        message.fileName = outcome.data.files[0].originalname
                        mMessageAdapter.addMessage(message)
                        saveMessage(message)

                        /*chat user save*/
                        val chatUser = ChatUser(
                            userId,
                            friendsPhoneno,
                            outcome.data.files[0].filename,
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
                            messageContent,
                            currentThreadTimeMillis,
                            imageUrl,
                            userId,
                            messageType,
                            outcome.data.files[0].originalname
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
                    val file = File(path)
                    val compressedImageFile = Compressor.compress(requireActivity(), file)

                    val imagePart = requireActivity().createMultiPart("file", compressedImageFile)
                    val messageType = "image"

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
            Log.i("pickPdf", imageFile.absolutePath.toString())

            showImageDialog(imageFile.absolutePath)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (requestCode == TAKE_PICTURE && resultCode == AppCompatActivity.RESULT_OK) {

            try {
                Log.i("pickPdf", image.toString())

                showImageDialog(image.toString())

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }else if (requestCode == PDF_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                val pdfData = data.data
                Log.d("pdfData","PDF DATA = $pdfData")

                val path = FilePath.getPath(requireActivity(), pdfData!!)
                val pdfPart = requireActivity().createMultiPartFile("file", path!!)
                val messageType = "pdf"
                uploadFile(userId, messageType, pdfPart)
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