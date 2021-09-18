package com.example.chatapp.model.repo.update_profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.update_profile.UpdateProfileRequest
import com.example.chatapp.model.pojo.update_profile.UpdateProfileResponse
import com.example.chatapp.model.repo.Outcome

class UpdateProfileRepositoryImpl(private val context: Context):UpdateProfileRepository {
    private val apiService = ApiClient.getInstance(context)

    override suspend fun updateProfile(
        username: String,
        phoneno: String,
        avatar: String,
        newPhoneno: String,
        token: String
    ): Outcome<UpdateProfileResponse> {
        val apiResponse = MutableLiveData<Outcome<UpdateProfileResponse>>()
        try {
            val response = apiService.updateProfile(UpdateProfileRequest(username,phoneno,avatar,newPhoneno),token)
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<UpdateProfileResponse>
    }
}