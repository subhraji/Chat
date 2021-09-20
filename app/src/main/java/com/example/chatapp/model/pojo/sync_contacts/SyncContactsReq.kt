package com.example.chatapp.model.pojo.sync_contacts

import com.google.gson.annotations.SerializedName

data class SyncContactsReq(
    @SerializedName("phone_list")
    val phone_list: List<String>,
)