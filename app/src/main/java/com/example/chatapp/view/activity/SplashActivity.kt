package com.example.chatapp.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.helper.transparentStatusBar

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({

            val sharedPreference = getSharedPreferences("TOKEN_PREF",
                Context.MODE_PRIVATE)
            if(sharedPreference.getBoolean("loginStatus",false) == true){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 1000)
    }
}