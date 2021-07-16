package com.example.chatapp.model.repo.login

import androidx.lifecycle.LiveData
import com.example.chatapp.model.pojo.req_otp.RequestOtp
import com.example.chatapp.model.repo.Outcome

interface LoginRepository {

    suspend fun reqOtp(mobile: String): Outcome<RequestOtp>
}