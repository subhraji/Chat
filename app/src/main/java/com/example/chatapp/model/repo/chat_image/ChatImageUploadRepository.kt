package com.example.chatapp.model.repo.chat_image

import com.example.chatapp.model.pojo.friend_chat.UploadImageResponse
import com.example.chatapp.model.repo.Outcome
import okhttp3.MultipartBody

interface ChatImageUploadRepository {

    suspend fun uploadChatImage(receiver_id: String,
                             messageType: String,
                             image: MultipartBody.Part?,
                             token: String): Outcome<UploadImageResponse>

}