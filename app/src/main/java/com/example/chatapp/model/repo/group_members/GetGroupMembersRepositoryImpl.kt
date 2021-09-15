package com.example.chatapp.model.repo.group_members

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.group_member.GroupMember
import com.example.chatapp.model.repo.Outcome

class GetGroupMembersRepositoryImpl(private val context: Context): GetGroupMembersRepository {

    private val apiService = ApiClient.getInstance(context)

    override suspend fun getGroupMember(token: String, groupId: String): Outcome<GroupMember> {
        val apiResponse = MutableLiveData<Outcome<GroupMember>>()
        try {
            val response = apiService.getGroupMembers(token, groupId)
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<GroupMember>
    }
}