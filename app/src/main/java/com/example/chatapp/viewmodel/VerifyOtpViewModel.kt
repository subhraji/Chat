package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.verify_otp.VerifyOtpRepository

class VerifyOtpViewModel(private val mVerifyOtpRepo: VerifyOtpRepository): ViewModel() {

    fun verifyOtp(mobile: String, otp: String) = liveData {
        emit(mVerifyOtpRepo.verifyOtp(mobile, otp))
    }
}