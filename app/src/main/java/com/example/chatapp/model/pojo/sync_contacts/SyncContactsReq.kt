package com.example.chatapp.model.pojo.sync_contacts

import com.google.gson.annotations.SerializedName

data class SyncContactsReq(
    @SerializedName("phoneno")
    val phoneno: String,
)