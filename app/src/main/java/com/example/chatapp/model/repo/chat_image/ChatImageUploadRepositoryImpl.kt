package com.example.chatapp.model.repo.chat_image

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.helper.toMultipartFormString
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.friend_chat.UploadImageResponse
import com.example.chatapp.model.repo.Outcome
import okhttp3.MultipartBody

class ChatImageUploadRepositoryImpl(context: Context) : ChatImageUploadRepository {

    private val apiService = ApiClient.getInstance(context)

    override suspend fun uploadChatImage(
        receiver_id: String,
        messageType: String,
        image: MultipartBody.Part?,
        token: String
    ): Outcome<UploadImageResponse> {
        val apiResponse = MutableLiveData<Outcome<UploadImageResponse>>()
        try {
            val response = apiService.uploadChatImage(
                receiver_id.toMultipartFormString(),
                messageType.toMultipartFormString(),
                image,
                token,
                )

            apiResponse.value = Outcome.success(response!!)

        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<UploadImageResponse>
    }
}