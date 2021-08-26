package com.example.chatapp.model.repo.get_profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.get_profile.GetProfile
import com.example.chatapp.model.repo.Outcome

class GetProfileRepositoryImpl(private val context: Context): GetProfileRepository {

    private val apiService = ApiClient.getInstance(context)

    override suspend fun getProfile(token: String): Outcome<GetProfile> {
        val apiResponse = MutableLiveData<Outcome<GetProfile>>()
        try {
            val response = apiService.getProfile(token)
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<GetProfile>
    }
}