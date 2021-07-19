package com.example.chatapp.model.pojo.sync_contacts


import com.google.gson.annotations.SerializedName

data class SyncContactsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("user_exists")
    val userExists: Boolean
)