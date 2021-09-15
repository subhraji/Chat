package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.group_members.GetGroupMembersRepository

class GetGroupMembersViewModel(private val getGroupMembersRepository: GetGroupMembersRepository): ViewModel() {
    fun getGroupMembers(token: String, groupId: String) = liveData {
        emit(getGroupMembersRepository.getGroupMember(token,groupId))
    }
}