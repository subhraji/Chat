package com.example.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.eduaid.child.models.db.room.chat.FriendChatDatabase
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.model.pojo.chat_user.ChatUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendChatViewModel(application: Application): AndroidViewModel(application) {
    private val db = FriendChatDatabase(this.getApplication())

    fun saveMessage(message: Message){
        CoroutineScope(Dispatchers.IO).launch {
            db.friendChatDao.insert(message)
        }
    }

    fun getChatMessages(userId: String) = liveData{
        emit(db.friendChatDao.getChatMessages(userId))
    }

    suspend fun getMessageCount(friendId: String): Int {
        return db.friendChatDao.getChatMessageCount(friendId)
    }

    fun deleteChat(message: Message){
        CoroutineScope(Dispatchers.IO).launch {
            db.friendChatDao.delete(message)
        }
    }
}