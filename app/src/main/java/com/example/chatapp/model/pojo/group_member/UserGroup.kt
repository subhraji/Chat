package com.example.chatapp.model.pojo.group_member


import com.google.gson.annotations.SerializedName

data class UserGroup(
    @SerializedName("is_admin")
    val isAdmin: Boolean
)