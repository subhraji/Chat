package com.example.chatapp.model.pojo.group_member


import com.google.gson.annotations.SerializedName

data class Group(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("createdBy")
    val createdBy: String,
    @SerializedName("groupImage")
    val groupImage: String,
    @SerializedName("groupName")
    val groupName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("users")
    val users: List<User>
)