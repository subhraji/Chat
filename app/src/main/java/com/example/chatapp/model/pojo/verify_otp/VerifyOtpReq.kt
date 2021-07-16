package com.example.chatapp.model.pojo.verify_otp

import com.google.gson.annotations.SerializedName

data class VerifyOtpReq(
    @SerializedName("phoneno")
    val phoneno: String,
    @SerializedName("otp")
    val otp: String,
)