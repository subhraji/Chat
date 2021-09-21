package com.example.chatapp.model.repo.sync_contacts

import com.example.chatapp.model.pojo.sync_contacts.SyncContactsResponse
import com.example.chatapp.model.repo.Outcome

interface SyncContactsRepository {
    suspend fun syncContacts(phonenoList: List<String>, token: String): Outcome<SyncContactsResponse>

}