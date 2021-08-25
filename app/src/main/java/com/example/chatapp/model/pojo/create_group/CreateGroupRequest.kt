package com.example.chatapp.model.pojo.create_group

import com.google.gson.annotations.SerializedName

data class CreateGroupRequest(
    @SerializedName("group_name")
    val group_name: String,
    @SerializedName("group_image")
    val group_image: String,
)