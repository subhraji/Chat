package com.example.chatapp.helper

import android.content.Context
import android.util.Log
import com.example.chatapp.model.network.APIConstants
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

class SocketHelper {

    companion object {
        private var mSocketIO: Socket? = null

        fun getSocket(nameSpace: String, context: Context): Socket? {

            val sharedPreference = context.getSharedPreferences("TOKEN_PREF",
                Context.MODE_PRIVATE)
            val accessToken = sharedPreference.getString("accessToken","name").toString()

            val token = accessToken

            return if (token.isNotEmpty()) {
                try {
                    mSocketIO = IO.socket(APIConstants.QUIZ_NODE_URL + nameSpace + "?token=$token")
                    mSocketIO?.let {
                        it.io().timeout(-1)
                        it.io().reconnection(true)
                    }
                    mSocketIO
                } catch (e: URISyntaxException) {
                    Log.d("error","socket error => ${e.message}")
                    e.printStackTrace()
                    mSocketIO
                }
            } else {
                mSocketIO
            }


        }

        fun loginSocket(context: Context): Socket? {

            val sharedPreference = context.getSharedPreferences("TOKEN_PREF",
                Context.MODE_PRIVATE)
            val accessToken = sharedPreference.getString("accessToken","name").toString()
            val token = accessToken

            return if (token.isNotEmpty()) {
                try {
                    mSocketIO = IO.socket("${APIConstants.QUIZ_NODE_URL}?token=$token")
                    mSocketIO?.let {
                        it.io().timeout(-1)
                        it.io().reconnection(true)
                    }
                    mSocketIO
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                    mSocketIO
                }
            } else {
                mSocketIO
            }


        }

        fun disConnect() {
            if (mSocketIO != null) {
                if (mSocketIO!!.connected()) {
                    mSocketIO!!.disconnect()
                }
            }
        }
    }
}