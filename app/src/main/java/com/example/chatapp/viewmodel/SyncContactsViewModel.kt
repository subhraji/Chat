package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.sync_contacts.SyncContactsRepository

class SyncContactsViewModel(private val mSyncContactsRepo: SyncContactsRepository): ViewModel()  {

    fun syncContacts(phonenoList: List<String>, token: String) = liveData {
        emit(mSyncContactsRepo.syncContacts(phonenoList, token))
    }
}