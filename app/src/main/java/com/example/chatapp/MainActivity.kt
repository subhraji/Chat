package com.example.chatapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.view.activity.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPreference = getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = sharedPreference.getString("accessToken","name").toString()

        Log.i("accessToken","token =>"+accessToken)


        logout_btn.setOnClickListener {
            val editor = sharedPreference.edit()
            editor.putString("accessToken","")
            editor.putString("refreshToken","")
            editor.putString("userId","")
            editor.putBoolean("loginStatus",false)
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}