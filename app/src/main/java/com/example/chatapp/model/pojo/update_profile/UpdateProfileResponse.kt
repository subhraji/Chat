package com.example.chatapp.model.pojo.update_profile


import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("user")
    val user: User
)