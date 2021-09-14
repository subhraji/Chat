package com.example.chatapp.view.fragment

import android.app.AlertDialog
import android.content.Context
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
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.helper.hideSoftKeyboard
import com.example.chatapp.model.pojo.group_chat.GroupMessage
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.example.chatapp.viewmodel.GroupChatViewModel
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
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupChatFragment : Fragment() {

    private lateinit var groupId: String
    private lateinit var groupName: String
    private lateinit var mGroupMessageListAdapter: GroupMessageListAdapter
    private var mSocket: Socket? = null
    private lateinit var accessToken: String
    private lateinit var phoneNo: String
    private lateinit var userId: String
    private val groupChatViewModel: GroupChatViewModel by viewModel()



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
            val bottomSheet = AddContactListFragment()
            val bundle = Bundle()
            bundle.putString("groupId", groupId)
            bottomSheet.arguments = bundle
            bottomSheet.show(requireActivity().supportFragmentManager, "AddContactList")
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

                    Log.i("checkGroup"," reached too")
                    val groupMessage = GroupMessage(
                        message.message,
                        message.messageUuid,
                        message.sentById,
                        message.sentByPhone,
                        message.media,
                        message.groupId
                    )
                    groupMessage.isSender = false
                    groupMessage.messageType = message.messageType

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


        val groupMessage = GroupMessage(
            messageText,
            msgUuid,
            userId,
            phoneNo,
            "",
            groupId
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
            "text"
        )
        textInput_gr.setText("")
        requireActivity().hideSoftKeyboard()
    }

    private fun emitMessage(
        msgUuid: String,
        messageText: String,
        image: String? = null,
        messageType: String = "text",
        fileName: String? = null
    ) {
        val jsonMessage = JSONObject()
        jsonMessage.put("messageUuid", msgUuid)
        jsonMessage.put("message", messageText)
        jsonMessage.put("messageType", messageType)
        if (image != null)
            jsonMessage.put("media", image)
        jsonMessage.put("fileName", fileName)
        jsonMessage.put("groupId", groupId)

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

    override fun onDestroy() {
        super.onDestroy()
        Log.i("socketOff","Done")
        mSocket?.let { socket ->
            socket.off(groupId, chatMessageListener)
        }
        mSocket?.disconnect()
    }

}