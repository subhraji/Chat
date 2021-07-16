package com.example.chatapp.model.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import java.io.IOException

internal class TokenRenewInterceptor(private val mContext: Context) : okhttp3.Interceptor {

    @SuppressLint("LogNotTimber")
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val response = chain.proceed(chain.request())

        // if 'x-auth-token' is available into the response header
        // save the new token into session.The header key can be
        // different upon implementation of backend.
        Log.d(TAG, "intercept: running")
        val newToken = response.header("token")

        val sessionCheck = response.header("sessionstatus")

        if (sessionCheck != null) {

            if (sessionCheck.toString() == "invalid") {
                Log.d(TAG, "Invalidated")

                //val prefManager = PrefManager(mContext)
                //prefManager.isFirstTimeLaunch = true
                //deleteAllData(mContext.applicationContext)
                //courseCoreDataViewModel.deleteCoreDataTable()
                //val i = Intent(mContext, LoginActivity::class.java)
                //i.putExtra("session", "Session Expired!")
                // set the new task and clear flags
                //i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //mContext.startActivity(i)

            }

        }


//        token renew
        /*if (newToken != null) {

            val realm: Realm = Realm.getDefaultInstance()

            realm.executeTransaction { bgRealm ->
                Log.d(TAG, "intercept: updating token here feb")
                val tokenObj = bgRealm.where(Token::class.java).findFirst()
                tokenObj?.token = newToken.addJWT()
            }
            //mTokenViewModel.updateToken(newToken.addJWT())
            Log.d(TAG, "intercept: token updated to ${newToken.addJWT()}")
        } else {
            Log.d(TAG, "intercept: no token available")
        }*/

        return response
    }

    companion object {
        private val TAG = "TokenRenewInterceptor"
    }
}