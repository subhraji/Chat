package com.eduaid.child.models.pojo.friend_chat
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Message2(
        @PrimaryKey(autoGenerate = false)
        @SerializedName("msgUuid")
        val msgUuid: String,
        @SerializedName("msg")
        val message: String,
        @SerializedName("image")
        val image: String?,
        @SerializedName("sentOn")
        val createdAt: Long,
        @SerializedName("userId")
        val userId: String
){
        var isSender = false
        var messageType = "text"
}
