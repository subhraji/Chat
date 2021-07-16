package com.example.chatapp.model.pojo.req_otp


import com.google.gson.annotations.SerializedName

data class RequestOtp(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)