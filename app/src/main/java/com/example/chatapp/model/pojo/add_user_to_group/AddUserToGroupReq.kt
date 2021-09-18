package com.example.chatapp.model.pojo.add_user_to_group

import com.google.gson.annotations.SerializedName

data class AddUserToGroupReq(
    @SerializedName("groupId")
    val groupId: String,
    @SerializedName("members")
    val members: List<String>,
)