package com.eduaid.child.models.pojo.friend_chat
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chatapp.model.pojo.friend_chat.User
import com.google.gson.annotations.SerializedName

@Entity
data class Message(
        @PrimaryKey(autoGenerate = false)
        @SerializedName("msgUuid")
        val msgUuid: String,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("image")
        val image: String?,
        @Embedded(prefix = "friend_")
        @SerializedName("sentBy")
        val sentBy: User,
        @SerializedName("sentOn")
        val sentOn: Long,

){
        var isSender = false
        var messageType = "text"
        var hasRead = false
        var isSent = false
        var isSeen = false
}
