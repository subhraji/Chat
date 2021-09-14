package com.example.chatapp.model.pojo.group_chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class GroupMessage(
    @SerializedName("message")
    val message: String,
    @PrimaryKey(autoGenerate = false)
    @SerializedName("messageUuid")
    val messageUuid: String,
    @SerializedName("sentById")
    val sentById: String,
    @SerializedName("sentByPhone")
    val sentByPhone: String,
    @SerializedName("media")
    val media: String?,
    @SerializedName("groupId")
    val groupId: String,
){
    var isSender = false
    var messageType = "text"
    var isSent = false
    var fileName: String? = null
}
