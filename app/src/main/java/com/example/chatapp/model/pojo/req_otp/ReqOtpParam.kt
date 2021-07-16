package com.example.chatapp.model.pojo.req_otp

import com.google.gson.annotations.SerializedName

data class ReqOtpParam (
    @SerializedName("phoneno")
    val mobile: String,
)
