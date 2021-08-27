package com.example.chatapp.model.pojo.update_profile


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar")
    val avatar: Any,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("online")
    val online: Boolean,
    @SerializedName("phoneno")
    val phoneno: String,
    @SerializedName("socketId")
    val socketId: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("username")
    val username: Any
)