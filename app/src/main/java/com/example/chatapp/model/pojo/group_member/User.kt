package com.example.chatapp.model.pojo.group_member


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,
    @SerializedName("online")
    val online: Boolean,
    @SerializedName("phoneno")
    val phoneno: String,
    @SerializedName("user_group")
    val userGroup: UserGroup,
    @SerializedName("username")
    val username: String
)