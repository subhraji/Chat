package com.example.chatapp.model.pojo.create_group


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Group(
    @SerializedName("createdBy")
    val createdBy: String,
    @SerializedName("groupImage")
    val groupImage: String,
    @SerializedName("groupName")
    val groupName: String,
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: String,
    @Embedded(prefix = "userGroup_")
    @SerializedName("user_group")
    val userGroup: UserGroup
)