package com.example.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.eduaid.child.models.db.room.chat.FriendChatDatabase
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.model.db.room.chat_user.ChatUserDatabase
import com.example.chatapp.model.db.room.create_group.CreateGroupDatabase
import com.example.chatapp.model.pojo.create_group.Group
import com.example.chatapp.model.repo.create_group.CreateGroupRepository
import com.example.chatapp.model.repo.sync_contacts.SyncContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateGroupViewModel(application: Application, private val mCreateGroupRepository: CreateGroupRepository): AndroidViewModel(application) {
    private val db = CreateGroupDatabase(this.getApplication())

    fun createGroup(group_name: String, group_image:String, token: String) = liveData {
        emit(mCreateGroupRepository.createGroup(group_name, group_image, token))
    }


    fun saveGroup(group: Group){
        CoroutineScope(Dispatchers.IO).launch {
            db.createGroupDao.insert(group)
        }
    }

    fun getGroups() = liveData{
        emit(db.createGroupDao.getGroups())
    }
}