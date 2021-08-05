package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.adapter.ContactListAdapter
import com.example.chatapp.adapter.MessageListAdapter
import com.example.chatapp.adapter.TestMessageListAdapter
import com.example.chatapp.helper.PaginationUtils
import com.example.chatapp.helper.PagingListener
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.helper.hideSoftKeyboard
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.sync_contacts.User
import com.example.chatapp.viewmodel.ChatUserViewModel
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_contacts_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val USER_ID = "userId"

class ChatFragment : Fragment() {
    private var mSocket: Socket? = null
    lateinit var accessToken: String
    lateinit var userId: String
    lateinit var friendsPhoneno: String
    private val chatViewModel: FriendChatViewModel by viewModel()
    private val chatUserViewModel: ChatUserViewModel by viewModel()
    private lateinit var mMessageAdapter: MessageListAdapter
    private var size = 0
    private var chatUserSize = 0
    private var page = 1
    private var rowPerPage = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID).toString()
            friendsPhoneno = it.getString("phoneno").toString()

        }
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

        mMessageAdapter = MessageListAdapter(mutableListOf(), requireActivity().supportFragmentManager)
        chat_recycler.apply {
            adapter = mMessageAdapter
        }

        initSocket()

        PushDownAnim.setPushDownAnimTo(cameraBtn).setOnClickListener {
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
        }

        mSocket?.connect()
        Log.d("Socket_connected", "Socket_connected => ${mSocket?.connected()}")
    }


    private val chatMessageListener = Emitter.Listener {
        requireActivity().runOnUiThread {
            val data = it[0] as JSONObject
            try {
                Log.d("data", "data => $data")
                //val isPending = data.getBoolean("isPending")
                val messageData = data.getJSONObject("data")
                val message = Gson().fromJson(messageData.toString(), Message::class.java)
                if (message.sentBy.id == userId) {

                    mMessageAdapter.addMessage(message)
                    saveMessage(message)

                    /*chat user save*/
                    val chatUser = ChatUser(
                        message.sentBy.id,
                        message.sentBy.phoneno,
                        message.msg,
                        "",
                        message.sentOn
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        chatUserSize = chatUserViewModel.isChatUserAvailable(message.sentBy.id)
                        Log.i("chatUserSize", "chat use size => ${chatUserSize}")
                    }

                    if(chatUserSize != 0){
                        updateChatUser(chatUser)
                    }else{
                        saveChatUser(chatUser)
                    }

                }
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
            com.example.chatapp.model.pojo.friend_chat.User(userId,"911"),
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
        if(chatUserSize != 0){
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

    private fun saveMessage(message: Message) {
        chat_recycler.scrollToPosition(mMessageAdapter.itemCount - 1)
        chatViewModel.saveMessage(message)
    }

    private fun getChatMessage(){
        chatViewModel.getChatMessages(userId).observe(requireActivity(), { messages ->
            Log.d("msgSize","msg list size = ${messages.size}")
            if (!messages.isNullOrEmpty()) {
                mMessageAdapter.addAllMessages(messages)
            } else {
                Log.d("msgSize","msg list size => ${messages.size}")
            }
        })
    }


    private fun saveChatUser(chatUser: ChatUser) {
        chatUserViewModel.saveChatUser(chatUser)
    }

    private fun updateChatUser(chatUser: ChatUser) {
        chatUserViewModel.updateChatUser(chatUser)
    }

    /*override fun onDestroy() {
        super.onDestroy()
        mSocket?.let { socket ->
            socket.off("chat message", chatMessageListener)
        }
        mSocket?.disconnect()
    }*/
}