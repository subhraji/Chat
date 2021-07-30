package com.example.chatapp.model.pojo.chat_user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class ChatUser(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("createdAt")
    val createdAt: Long,
){
    var hasRead = true
}