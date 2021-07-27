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
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.R
import com.example.chatapp.helper.SocketHelper
import com.example.chatapp.helper.hideSoftKeyboard
import com.example.chatapp.viewmodel.FriendChatViewModel
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val USER_ID = "userId"

class ChatFragment : Fragment() {
    private var mSocket: Socket? = null
    lateinit var accessToken: String
    lateinit var userId: String
    private val chatViewModel: FriendChatViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID).toString()
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

        Log.i("userId",userId)
        initSocket()


        PushDownAnim.setPushDownAnimTo(cameraBtn).setOnClickListener {

        }
        PushDownAnim.setPushDownAnimTo(chat_btnSend).setOnClickListener {
            sendMessage()
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
                if (message.userId == userId) {
                    saveMessage(message)
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
            null,
            currentThreadTimeMillis,
            userId
        )
        //message.isSender = true
        //message.messageType = "text"
        saveMessage(message)

        Log.d("emitting send message","emitting send message")
        emitMessage(
            msgUuid,
            messageText,
            currentThreadTimeMillis,
            null,
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
        chatViewModel.saveMessage(message)
    }
}