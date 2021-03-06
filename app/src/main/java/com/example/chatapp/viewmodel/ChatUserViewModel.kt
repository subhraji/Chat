package com.example.chatapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.model.db.room.chat_user.ChatUserDatabase
import com.example.chatapp.model.pojo.chat_user.ChatUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatUserViewModel(application: Application): AndroidViewModel(application) {

    private val db = ChatUserDatabase(this.getApplication())

    fun saveChatUser(chatUser: ChatUser){
        CoroutineScope(Dispatchers.IO).launch {
            db.chatUserDao.insert(chatUser)
        }
    }

    fun deleteChatUser(chatUser: ChatUser){
        CoroutineScope(Dispatchers.IO).launch {
            db.chatUserDao.delete(chatUser)
        }
    }

    fun getChatUser() = liveData{
        emit(db.chatUserDao.getChatUser())
    }

    suspend fun getChatUserCount(): Int {
        return db.chatUserDao.getChatUserCount()
    }

    suspend fun isChatUserAvailable(userId: String): Int {
        return db.chatUserDao.isChatUserAvailable(userId)
    }

    fun updateChatUser(chatUser: ChatUser){
        CoroutineScope(Dispatchers.IO).launch {
            db.chatUserDao.update(chatUser)
        }
    }

    suspend fun updateChatUser2(message: String, userId: String){
        return db.chatUserDao.updateChatUser(message,userId)
    }

}