package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.login.LoginRepository

class LoginViewModel(private val mLoginRepo: LoginRepository): ViewModel() {

    fun reqOtp(mobile: String) = liveData {
        emit(mLoginRepo.reqOtp(mobile))
    }
}