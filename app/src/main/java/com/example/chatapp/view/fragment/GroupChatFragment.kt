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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.adapter.GroupMessageListAdapter
import com.example.chatapp.helper.*
import com.example.chatapp.model.network.APIConstants
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.friend_chat.User
import com.example.chatapp.model.pojo.group_chat.GroupMessage
import com.example.chatapp.model.repo.Outcome
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.example.chatapp.viewmodel.GroupChatViewModel
import com.example.chatapp.viewmodel.UploadChatImageViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group_chat.*
import kotlinx.android.synthetic.main.fragment_main.*
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

class GroupChatFragment : Fragment(), UploadImageListener {

    private lateinit var groupId: String
    private lateinit var groupName: String
    private var isAdmin: Boolean = false

    private lateinit var mGroupMessageListAdapter: GroupMessageListAdapter
    private var mSocket: Socket? = null
    private lateinit var accessToken: String
    private lateinit var phoneNo: String
    private lateinit var userId: String
    private val groupChatViewModel: GroupChatViewModel by viewModel()
    private val uploadChatImageViewModel: UploadChatImageViewModel by viewModel()

    private var image: String? = null
    private var imageUri: Uri? = null
    private val pickImage = 100
    private val TAKE_PICTURE = 2
    private val PDF_REQUEST_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            groupId = it.getString("groupId").toString()
            groupName = it.getString("groupName").toString()
            isAdmin = it.getBoolean("isAdmin")
            Log.i("isAdmin",isAdmin.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        group_name_gr.text = groupName

        mGroupMessageListAdapter = GroupMessageListAdapter(mutableListOf(), requireActivity().supportFragmentManager)
        chat_recycler_gr.apply {
            adapter = mGroupMessageListAdapter
        }

        PushDownAnim.setPushDownAnimTo(chat_frg_back_arrow_gr).setOnClickListener {
            findNavController().navigateUp()
        }


        initSocket()


        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()
        userId = sharedPreference.getString("userId","userId").toString()
        phoneNo = sharedPreference.getString("phoneno","phoneno").toString()

        PushDownAnim.setPushDownAnimTo(cameraBtn_gr).setOnClickListener {
            selectImage()
        }

        PushDownAnim.setPushDownAnimTo(chat_btnSend_gr).setOnClickListener {
            sendMessage()
        }

        mSocket?.emit("join-group", groupId, Ack {
            Log.d("join: ","join")
        })

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                getGroupMessage()
            }
        }


        PushDownAnim.setPushDownAnimTo(group_option_btn).setOnClickListener {

            val popupMenu: PopupMenu = PopupMenu(requireContext(),group_option_btn)
            popupMenu.menuInflater.inflate(R.menu.group_chat_option_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {

                when(it.itemId){
                    R.id.group_info -> {
                        val bundle = Bundle()
                        bundle.putString("groupId", groupId)
                        bundle.putBoolean("isAdmin", isAdmin)
                        findNavController().navigate(R.id.groupInfoFragment, bundle)
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

    }

    private fun initSocket() {
        while (mSocket == null) {
            mSocket = SocketHelper.loginSocket(requireContext())
        }
        //listeners
        mSocket?.let { socket ->
            socket.on(groupId, chatMessageListener)
        }

        mSocket?.connect()
        Log.d("group_socket", "Socket_connected => ${mSocket?.connected()}")
    }

    private val chatMessageListener = Emitter.Listener {
        requireActivity().runOnUiThread {

            val data = it[0] as JSONObject
            Log.i("groupChatData","groupChatData => ${data}")
            val messageData = data.getJSONObject("data")
            val message = Gson().fromJson(messageData.toString(), GroupMessage::class.java)
            try {

                Log.i("checkGroup"," reached")
                Log.i("checkGroup",message.message)

                if(message != null){
                    val msgContent = if (message.messageType == "pdf") {
                        message.fileName
                    } else {
                        message.message
                    }
                    Log.i("checkGroup"," reached too: ${message.sentOn}")
                    val groupMessage = GroupMessage(
                        message.message,
                        message.messageUuid,
                        message.sentById,
                        message.sentByPhone,
                        message.media,
                        message.groupId,
                        message.sentOn,
                    )
                    groupMessage.isSender = false
                    groupMessage.messageType = message.messageType
                    groupMessage.fileName = message.fileName

                    mGroupMessageListAdapter.addMessage(groupMessage)
                    saveMessage(groupMessage)

                }


            } catch (e: JSONException) {
                Log.d("Socket_connected","exception -> ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun sendMessage() {
        val messageText = textInput_gr.text.toString().trim()
        if (messageText.isEmpty()) {
            textInput_gr.error = "message cannot be empty"
            return
        }
        //val id = (0..1).random()
        val currentThreadTimeMillis = System.currentTimeMillis()
        val msgUuid = currentThreadTimeMillis.toString()

        Log.i("sentOn", currentThreadTimeMillis.toString())

        val groupMessage = GroupMessage(
            messageText,
            msgUuid,
            userId,
            phoneNo,
            "",
            groupId,
            currentThreadTimeMillis
        )
        groupMessage.isSender = true
        groupMessage.messageType = "text"
        groupMessage.isSent = true

        mGroupMessageListAdapter.addMessage(groupMessage)
        saveMessage(groupMessage)

        Log.d("emitting send message","emitting send message")
        emitMessage(
            msgUuid,
            messageText,
            null,
            "text",
            null,
            currentThreadTimeMillis
        )
        textInput_gr.setText("")
        requireActivity().hideSoftKeyboard()
    }

    private fun emitMessage(
        msgUuid: String,
        messageText: String,
        image: String? = null,
        messageType: String = "text",
        fileName: String? = null,
        currentThreadTimeMillis: Long,
        ) {
        val jsonMessage = JSONObject()
        jsonMessage.put("messageUuid", msgUuid)
        jsonMessage.put("message", messageText)
        jsonMessage.put("messageType", messageType)
        if (image != null)
            jsonMessage.put("media", image)
            jsonMessage.put("fileName", fileName)
        jsonMessage.put("fileName", fileName)
        jsonMessage.put("groupId", groupId)
        jsonMessage.put("sentOn", currentThreadTimeMillis)

        Log.d("Emit","emit started")
        mSocket?.emit("group message", jsonMessage.toString(), Ack {
            val isBlocked = it[0]
            Log.d("blocked","blocked = $isBlocked")
        })
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

    private fun saveMessage(groupMessage: GroupMessage) {
        Log.i("saveMessage","reached here...")
        chat_recycler_gr.scrollToPosition(mGroupMessageListAdapter.itemCount - 1)
        groupChatViewModel.saveGroupMessage(groupMessage)
    }

    private fun getGroupMessage(){
        groupChatViewModel.getGroupMessages(groupId).observe(requireActivity(), { messages ->
            Log.d("msgSize","msg list size = ${messages.size}")
            if (!messages.isNullOrEmpty()) {

                mGroupMessageListAdapter.addAllMessages(messages)

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

                        val groupMessage = GroupMessage(
                            messageContent,
                            msgUuid,
                            userId,
                            phoneNo,
                            imageUrl,
                            groupId,
                            currentThreadTimeMillis
                        )
                        groupMessage.isSender = true
                        groupMessage.messageType = messageType
                        groupMessage.isSent = true
                        mGroupMessageListAdapter.addMessage(groupMessage)

                        saveMessage(groupMessage)

                        Log.d("emitting send message","emitting send message")
                        emitMessage(
                            msgUuid,
                            messageContent,
                            imageUrl,
                            messageType,
                            outcome.data.files[0].filename,
                            currentThreadTimeMillis
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
            socket.off(groupId, chatMessageListener)
        }
        mSocket?.disconnect()
    }

}