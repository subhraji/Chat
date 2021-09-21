package com.example.chatapp.model.repo.sync_contacts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsReq
import com.example.chatapp.model.pojo.sync_contacts.SyncContactsResponse
import com.example.chatapp.model.repo.Outcome

class SyncContactsRepositoryImpl(private val context: Context):SyncContactsRepository {

    private val apiService = ApiClient.getInstance(context)

    override suspend fun syncContacts(
        phonenoList: List<String>,
        token: String
    ): Outcome<SyncContactsResponse> {
        val apiResponse = MutableLiveData<Outcome<SyncContactsResponse>>()
        try {
            val response = apiService.syncContacts(SyncContactsReq(phonenoList),token)
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<SyncContactsResponse>
    }
}