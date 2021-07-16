package com.example.chatapp.model.repo.verify_otp

import com.example.chatapp.model.pojo.req_otp.RequestOtp
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpResponse
import com.example.chatapp.model.repo.Outcome

interface VerifyOtpRepository  {

    suspend fun verifyOtp(phoneno: String, otp: String): Outcome<VerifyOtpResponse>

}