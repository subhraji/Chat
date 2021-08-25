package com.example.chatapp.model.pojo.create_group


import com.google.gson.annotations.SerializedName

data class UserGroup(
    @SerializedName("is_admin")
    val isAdmin: Boolean
)