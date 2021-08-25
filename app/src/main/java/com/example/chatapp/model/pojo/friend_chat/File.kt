package com.example.chatapp.model.pojo.friend_chat


import com.google.gson.annotations.SerializedName

data class File(
    @SerializedName("destination")
    val destination: String,
    @SerializedName("encoding")
    val encoding: String,
    @SerializedName("fieldname")
    val fieldname: String,
    @SerializedName("filename")
    val filename: String,
    @SerializedName("mimetype")
    val mimetype: String,
    @SerializedName("originalname")
    val originalname: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("size")
    val size: Int
)