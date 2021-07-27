package com.example.chatapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.example.chatapp.helper.removeStatusBar
import com.example.chatapp.helper.transparentStatusBar
import com.example.chatapp.view.activity.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.removeStatusBar()
        transparentStatusBar()
        setContentView(R.layout.activity_main)

        val sharedPreference = getSharedPreferences("TOKEN_PREF",
            Context.MODE_PRIVATE)
        accessToken = sharedPreference.getString("accessToken","name").toString()

    }
}