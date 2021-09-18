package com.example.chatapp.model.pojo.add_user_to_group


import com.google.gson.annotations.SerializedName

data class AddUserToGroupResponse(
    @SerializedName("group")
    val group: Group,
    @SerializedName("status")
    val status: String
)