package com.example.chatapp.model.pojo.get_profile


import com.google.gson.annotations.SerializedName

data class GetProfile(
    @SerializedName("status")
    val status: String,
    @SerializedName("user")
    val user: User
)