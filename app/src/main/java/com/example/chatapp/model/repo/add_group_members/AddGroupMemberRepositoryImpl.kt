package com.example.chatapp.model.repo.add_group_members

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.add_user_to_group.AddUserToGroupReq
import com.example.chatapp.model.pojo.add_user_to_group.AddUserToGroupResponse
import com.example.chatapp.model.repo.Outcome

class AddGroupMemberRepositoryImpl(private val context: Context):AddGroupMemberRepository {
    private val apiService = ApiClient.getInstance(context)

    override suspend fun addGroupMembers(
        groupId: String,
        members: List<String>,
        token: String
    ): Outcome<AddUserToGroupResponse> {
        val apiResponse = MutableLiveData<Outcome<AddUserToGroupResponse>>()
        try {
            val response = apiService.addGroupMember(AddUserToGroupReq(groupId,members),token)
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<AddUserToGroupResponse>
    }

}