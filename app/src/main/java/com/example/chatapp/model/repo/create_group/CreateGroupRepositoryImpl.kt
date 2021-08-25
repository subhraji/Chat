package com.example.chatapp.model.repo.create_group

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.model.network.ApiClient
import com.example.chatapp.model.pojo.create_group.CreateGroupRequest
import com.example.chatapp.model.pojo.create_group.CreateGroupResponse
import com.example.chatapp.model.repo.Outcome

class CreateGroupRepositoryImpl(private val context: Context):CreateGroupRepository {
    private val apiService = ApiClient.getInstance(context)

    override suspend fun createGroup(
        group_name: String,
        group_image: String,
        token: String
    ): Outcome<CreateGroupResponse> {
        val apiResponse = MutableLiveData<Outcome<CreateGroupResponse>>()
        try {
            val response = apiService.createGroup(CreateGroupRequest(group_name,group_image),token)
            apiResponse.value = Outcome.success(response!!)
        } catch (e: Throwable) {
            apiResponse.value = Outcome.failure(e)
        }

        return apiResponse.value as Outcome<CreateGroupResponse>
    }

}