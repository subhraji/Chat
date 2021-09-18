package com.example.chatapp.model.repo.add_group_members

import com.example.chatapp.model.pojo.add_user_to_group.AddUserToGroupResponse
import com.example.chatapp.model.repo.Outcome

interface AddGroupMemberRepository {
    suspend fun addGroupMembers(groupId: String, members: List<String>,token: String): Outcome<AddUserToGroupResponse>
}