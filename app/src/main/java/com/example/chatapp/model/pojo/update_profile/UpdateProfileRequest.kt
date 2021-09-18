package com.example.chatapp.model.pojo.update_profile

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("phoneno")
    val phoneno: String,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("newPhoneNumber")
    val newPhoneNumber: String,
)