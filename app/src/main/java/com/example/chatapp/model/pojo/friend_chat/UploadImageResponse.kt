package com.example.chatapp.model.pojo.friend_chat


import com.google.gson.annotations.SerializedName

data class UploadImageResponse(
    @SerializedName("files")
    val files: List<File>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)