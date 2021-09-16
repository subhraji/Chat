package com.example.chatapp.model.pojo.friend_chat

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("phoneno")
    val phoneno: String,
    @SerializedName("avatar")
    val avatar: String,
    )