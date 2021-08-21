package com.example.chatapp.model.repo.chat_image

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.helper.toMultipartFormString
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.network.ApiInterface
import com.example.chatapp.model.pojo.friend_chat.UploadImageReq
import com.example.chatapp.model.pojo.friend_chat.UploadImageResponse
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsReq
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsResponse
import com.example.chatapp.model.repo.ApiResponse
import com.example.chatapp.model.repo.Outcome
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
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