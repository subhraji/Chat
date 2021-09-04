package com.example.chatapp.model.pojo.add_user_to_group


import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("createdBy")
    val createdBy: String,
    @SerializedName("groupImage")
    val groupImage: String,
    @SerializedName("groupName")
    val groupName: String,
    @SerializedName("id")
    val id: String
)