package com.example.chatapp.model.repo.create_group

import com.example.chatapp.model.pojo.create_group.CreateGroupResponse
import com.example.chatapp.model.repo.Outcome

interface CreateGroupRepository {
    suspend fun createGroup(group_name: String, group_image: String, token: String): Outcome<CreateGroupResponse>

}