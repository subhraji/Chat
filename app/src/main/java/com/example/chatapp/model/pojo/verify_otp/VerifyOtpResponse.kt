package com.example.chatapp.model.pojo.verify_otp


import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("userId")
    val userId: String
)