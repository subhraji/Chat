package com.example.chatapp.model.pojo.friend_chat

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class UploadImageReq (
    @SerializedName("receiver_id")
    val receiver_id: String,
    @SerializedName("message_type")
    val message_type: String,
    @SerializedName("image")
    val image: MultipartBody.Part?,
)