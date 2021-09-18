package com.example.chatapp.model.pojo.create_group


import com.google.gson.annotations.SerializedName

data class CreateGroupResponse(
    @SerializedName("group")
    val group: Group,
    @SerializedName("status")
    val status: String
)