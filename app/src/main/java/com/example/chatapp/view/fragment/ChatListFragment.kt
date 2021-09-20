package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.adapter.ChatUserAdapter
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.model.pojo.chat_user.ChatUser
import com.example.chatapp.viewmodel.ChatUserViewModel
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat_list.*
import kotlinx.android.synthetic.main.fragment_contacts_list.*
import kotlinx.android.synthetic.main.item_chat_user.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatListFragment : Fragment(), ChatUserAdapter.ChatUserItemClickClickLister {
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
        //mChatUserAdapter = ChatUserAdapter(mutableListOf(), requireActivity())


        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        mChatUserAdapter = ChatUserAdapter(mutableListOf(),requireActivity(), this@ChatListFragment)
        chat_user_recycler.apply {
            adapter = mChatUserAdapter
        }


        CoroutineScope(Dispatchers.IO).launch {
            size = chatUserViewModel.getChatUserCount()
            Log.d("list_size","list size => ${size}")
            withContext(Dispatchers.Main) {

                initSocket()

                getChatUser()
            }
        }


        PushDownAnim.setPushDownAnimTo(get_contacts_btn).setOnClickListener {

            Log.i("socketOff","Done here...chat list")
            mSocket?.let { socket ->
                socket.off("chat message", chatMessageListener)
            }
            mSocket?.disconnect()

            findNavController().navigate(R.id.action_mainFragment_to_contactsListFragment)

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

                if(message!=null){

                    val messages = com.eduaid.child.models.pojo.friend_chat.Message(
                        message.msgUuid,
                        message.msg,
                        message.image,
                        com.example.chatapp.model.pojo.friend_chat.User(message.sentBy.id,message.sentBy.phoneno,message.sentBy.avatar),
                        message.sentOn,
                    )
                    messages.isSender = false
                    messages.messageType = "text"


                    saveMessage(messages)

                    /*chat user save*/

                    CoroutineScope(Dispatchers.IO).launch {
                        chatUserSize = chatUserViewModel.isChatUserAvailable(message.sentBy.id)
                        Log.i("chatUserSize", "chat use size => ${chatUserSize}")

                        withContext(Dispatchers.Main) {

                            val chatUser = ChatUser(
                                message.sentBy.id,
                                message.sentBy.phoneno,
                                message.msg,
                                message.sentBy.avatar,
                                message.sentOn
                            )


                            if(chatUserSize == 1){
                                Log.d("check","reached here")
                                updateChatUser(chatUser)
                                getChatUser()
                                //mChatUserAdapter.addUser(chatUser)


                            }else{
                                Log.d("check","reached here too => ${chatUserSize}")
                                saveChatUser(chatUser)
                                getChatUser()
                                //mChatUserAdapter.addUser(chatUser)

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

    private fun saveMessage(message: Message) {
        Log.i("saveMessage", message.msg)
        chatViewModel.saveMessage(message)
    }

    private fun getChatUser(){
        chatUserViewModel.getChatUser().observe(requireActivity(), { users ->
            Log.d("userSize","user list size = ${users.size}")
            if (!users.isNullOrEmpty()) {
                Log.d("userSize","user list size too = ${users.size}")

                //mChatUserAdapter.addAllUsers(users)
                chat_user_recycler.adapter = ChatUserAdapter(users.toMutableList(),requireActivity(),this@ChatListFragment)
                //mChatUserAdapter.addAllChatUser(users)

            } else {
                Log.d("userSize","user list size => ${users.size}")
            }
        })
    }

    private fun saveChatUser(chatUser: ChatUser) {
        Log.i("saveChatUser","reached here too...")
        chatUserViewModel.saveChatUser(chatUser)
    }

    private fun deleteChatUser(chatUser: ChatUser) {
        chatUserViewModel.deleteChatUser(chatUser)
    }

    private fun updateChatUser(chatUser: ChatUser) {
        chatUserViewModel.updateChatUser(chatUser)
    }

    private suspend fun updateChatUser2(message: String, userId: String) {
        chatUserViewModel.updateChatUser2(message, userId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("socketOff","Done here...chat list")
        mSocket?.let { socket ->
            socket.off("chat message", chatMessageListener)
        }
        mSocket?.disconnect()
    }

    override fun onItemClicked(view: View, position: Int) {

        Log.i("socketOff","Done on chat list")
        mSocket?.let { socket ->
            socket.off("chat message", chatMessageListener)
        }
        mSocket?.disconnect()

        val users = view.tag as ChatUser
        val bundle = bundleOf("userId" to users.userId, "phoneno" to users.userName, "avatar" to users.image)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment, bundle)
    }
}