package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.adapter.ChatUserAdapter
import com.example.chatapp.adapter.ContactListAdapter
import com.example.chatapp.adapter.MessageListAdapter
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.model.pojo.sync_contacts.User
import com.example.chatapp.viewmodel.ChatUserViewModel
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_contacts_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatListFragment : Fragment() {
    private var mSocket: Socket? = null
    lateinit var accessToken: String
    private val chatUserViewModel: ChatUserViewModel by viewModel()
    private val chatViewModel: FriendChatViewModel by viewModel()
    private var size = 0
    private lateinit var mChatUserAdapter: ChatUserAdapter
    private var chatUserSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mChatUserAdapter = ChatUserAdapter(mutableListOf(), requireActivity())


        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        mChatUserAdapter = ChatUserAdapter(mutableListOf(),requireActivity())
        chat_user_recycler.apply {
            adapter = mChatUserAdapter
        }

        initSocket()

        CoroutineScope(Dispatchers.IO).launch {
            size = chatUserViewModel.getChatUserCount()
            Log.d("list_size","list size => ${size}")
            withContext(Dispatchers.Main) {
                getChatUser()
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
                val isPending = data.getBoolean("isPending")
                val messageData = data.getJSONObject("data")
                val message = Gson().fromJson(messageData.toString(), Message::class.java)

                    saveMessage(message)

                    /*chat user save*/
                val chatUser = ChatUser(
                    message.userId,
                    "unknown",
                    message.message,
                    "",
                    message.createdAt
                )

                CoroutineScope(Dispatchers.IO).launch {
                    chatUserSize = chatUserViewModel.isChatUserAvailable(message.userId)
                    Log.i("chatUserSize", "chat use size => ${chatUserSize}")
                }

                if(chatUserSize>0){
                        updateChatUser(chatUser)
                        getChatUser()
                    }else{
                        saveChatUser(chatUser)
                        getChatUser()
                    }

            } catch (e: JSONException) {
                Log.d("Socket_connected","exception -> ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun saveMessage(message: Message) {
        chatViewModel.saveMessage(message)
    }

    private fun getChatUser(){
        chatUserViewModel.getChatUser().observe(requireActivity(), { users ->
            Log.d("userSize","user list size = ${users.size}")
            if (!users.isNullOrEmpty()) {
                chat_user_recycler.adapter = ChatUserAdapter(users,requireActivity())
            } else {
                Log.d("userSize","user list size => ${users.size}")
            }
        })
    }

    private fun saveChatUser(chatUser: ChatUser) {
        chatUserViewModel.saveChatUser(chatUser)
    }

    private fun updateChatUser(chatUser: ChatUser) {
        chatUserViewModel.updateChatUser(chatUser)
    }
}