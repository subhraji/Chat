package com.example.chatapp

import android.app.Application
import appModule
import org.koin.android.java.KoinAndroidApplication
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        val koin = KoinAndroidApplication.create(this)
            .modules(appModule)
        startKoin(koin)
    }
}