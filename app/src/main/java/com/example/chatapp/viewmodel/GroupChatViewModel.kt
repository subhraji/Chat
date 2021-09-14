package com.example.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.chatapp.model.db.room.group_chat.GroupChatDatabase
import com.example.chatapp.model.pojo.group_chat.GroupMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChatViewModel(application: Application): AndroidViewModel(application) {

    private val db = GroupChatDatabase(this.getApplication())

    fun saveGroupMessage(groupMessage : GroupMessage){
        CoroutineScope(Dispatchers.IO).launch {
            db.groupChatDao.insert(groupMessage)
        }
    }


    fun getGroupMessages(groupId: String) = liveData{
        emit(db.groupChatDao.getGroupMessages(groupId))
    }

}