package com.example.chatapp.model.repo.group_members

import com.example.chatapp.model.pojo.group_member.GroupMember
import com.example.chatapp.model.repo.Outcome

interface GetGroupMembersRepository {
    suspend fun getGroupMember(token: String, groupId: String): Outcome<GroupMember>
}