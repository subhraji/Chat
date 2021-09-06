package com.example.chatapp.view.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.adapter.GroupMessageListAdapter
import com.example.chatapp.adapter.MessageListAdapter
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.helper.hideSoftKeyboard
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.group_chat.GroupMessage
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class GroupChatFragment : Fragment() {

    private lateinit var groupId: String
    private lateinit var groupName: String
    private lateinit var mGroupMessageListAdapter: GroupMessageListAdapter
    private var mSocket: Socket? = null
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            groupId = it.getString("groupId").toString()
            groupName = it.getString("groupName").toString()

            Log.i("groupId",groupId)
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

        PushDownAnim.setPushDownAnimTo(add_member_btn).setOnClickListener {
            val bottomSheet = AddContactList()
            val bundle = Bundle()
            bundle.putString("groupId", groupId)
            bottomSheet.arguments = bundle
            bottomSheet.show(requireActivity().supportFragmentManager, "AddContactList")
        }

        initSocket()

        PushDownAnim.setPushDownAnimTo(cameraBtn_gr).setOnClickListener {
            selectImage()
        }

        PushDownAnim.setPushDownAnimTo(chat_btnSend_gr).setOnClickListener {
            sendMessage()
        }

    }

    private fun initSocket() {
        while (mSocket == null) {
            mSocket = SocketHelper.loginSocket(requireContext())
        }
        //listeners
        mSocket?.let { socket ->
            socket.on("group message", chatMessageListener)
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
                val message = Gson().fromJson(messageData.toString(), GroupMessage::class.java)

                    Log.d("data","data -> ${data}")

            } catch (e: JSONException) {
                Log.d("Socket_connected","exception -> ${e.message}")
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
            com.example.chatapp.model.pojo.friend_chat.User(userId,"friendsPhoneno"),
            currentThreadTimeMillis,
        )
        message.isSender = true
        message.messageType = "text"
        message.isSent = true

        mGroupMessageListAdapter.addMessage(message)


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
                    //dispatchCameraIntent()
                }
                "Choose from Gallery" -> {
                    //dispatchGalleryIntent()
                }
                "Choose Documents" -> {
                    //dispatchPdfIntent()
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }


}