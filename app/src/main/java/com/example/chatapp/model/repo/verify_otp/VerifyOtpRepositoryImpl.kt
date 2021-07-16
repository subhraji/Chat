package com.example.chatapp.model.repo.verify_otp

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.req_otp.ReqOtpParam
import com.example.chatapp.model.pojo.req_otp.RequestOtp
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpReq
import com.example.chatapp.model.pojo.verify_otp.VerifyOtpResponse
import com.example.chatapp.model.repo.Outcome

class VerifyOtpRepositoryImpl(private val context: Context): VerifyOtpRepository {

    private val apiService = ApiClient.getInstance(context)

    override suspend fun verifyOtp(phoneno: String, otp: String): Outcome<VerifyOtpResponse> {
        val apiResponse = MutableLiveData<Outcome<VerifyOtpResponse>>()
        try {
            val response = apiService.verifyOtp(VerifyOtpReq(phoneno, otp))
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<VerifyOtpResponse>
    }
}