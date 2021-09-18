package com.example.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.repo.add_group_members.AddGroupMemberRepository

class AddGroupMembersViewModel(private val mAddGroupMemberRepository: AddGroupMemberRepository): ViewModel() {

    fun addGroupMembers(groupId: String, members:List<String>, token: String) = liveData {
        emit(mAddGroupMemberRepository.addGroupMembers(groupId,members, token))
    }
}