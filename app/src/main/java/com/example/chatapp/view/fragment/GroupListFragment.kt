package com.example.chatapp.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.helper.NewGroupListener
import com.example.chatapp.helper.gone
import com.example.chatapp.helper.visible
import com.github.nkzawa.socketio.client.Socket
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group_list.*

class GroupListFragment() : Fragment() {
    private var mSocket: Socket? = null
    lateinit var accessToken: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        create_group_card.gone()

        val sharedPreference = requireActivity().getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = "JWT "+sharedPreference.getString("accessToken","name").toString()

        PushDownAnim.setPushDownAnimTo(create_group_btn).setOnClickListener {
            create_group_card.visible()
        }

        PushDownAnim.setPushDownAnimTo(create_group_btnn).setOnClickListener {
            create_group_card.gone()
        }

    }

    /*private fun initSocket() {
        while (mSocket == null) {
            mSocket = SocketHelper.loginSocket(requireContext())
        }
        val currentThreadTimeMillis = System.currentTimeMillis()
        val groupId = "group"+currentThreadTimeMillis.toString()
        //listeners
        mSocket?.let { socket ->
            socket.on(groupId, groupChatMessageListener)
        }

        mSocket?.connect()
        Log.d("Socket_connected", "Socket_connected => ${mSocket?.connected()}")
    }

    private val groupChatMessageListener = Emitter.Listener {
        requireActivity().runOnUiThread {
            Log.i("checkList","reached here")

            val data = it[0] as JSONObject
            try {
                val messageData = data.getJSONObject("data")
                val message = Gson().fromJson(messageData.toString(), Message::class.java)

            } catch (e: JSONException) {
                Log.d("Socket_connected","exception -> ${e.message}")
                e.printStackTrace()
            }
        }
    }*/


}