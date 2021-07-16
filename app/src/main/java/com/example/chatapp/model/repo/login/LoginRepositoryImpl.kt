package com.example.chatapp.model.repo.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.req_otp.ReqOtpParam
import com.example.chatapp.model.pojo.req_otp.RequestOtp
import com.example.chatapp.model.repo.ApiResponse
import com.example.chatapp.model.repo.Outcome
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class LoginRepositoryImpl(private val context: Context): LoginRepository {
    private val apiService = ApiClient.getInstance(context)


    override suspend fun reqOtp(mobile: String): Outcome<RequestOtp> {
        val apiResponse = MutableLiveData<Outcome<RequestOtp>>()
        try {
            val response = apiService.reqOtp(ReqOtpParam(mobile))
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<RequestOtp>
    }

}