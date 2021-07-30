package com.example.chatapp.model.db.room.chat_user

import androidx.room.Dao
import androidx.room.Query
import com.example.chatapp.model.db.room.BaseDao
import com.example.chatapp.model.pojo.chat_user.ChatUser

@Dao
interface ChatUserDao: BaseDao<ChatUser> {

    @Query("Select * from chatuser order by createdAt")
    suspend fun getChatUser(): List<ChatUser>

    @Query("Select count(*) from chatuser")
    suspend fun getChatUserCount(): Int
}