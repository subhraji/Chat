package com.example.chatapp.model.pojo.group_chat

import androidx.room.Embedded
import com.example.chatapp.model.pojo.friend_chat.User
import com.google.gson.annotations.SerializedName

data class GroupMessage(
    @SerializedName("messageUuid")
    val messageUuid: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("media")
    val media: String?,
    @SerializedName("groupId")
    val groupId: String?,
){
    var isSender = false
    var messageType = "text"
    var isSent = false
    var fileName: String? = null
}