package com.example.chatapp.model.pojo.group_member


import com.google.gson.annotations.SerializedName

data class GroupMember(
    @SerializedName("group")
    val group: Group,
    @SerializedName("status")
    val status: String
)