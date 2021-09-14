package com.example.chatapp.model.db.room.group_chat

import androidx.room.Dao
import androidx.room.Query
import com.example.chatapp.model.db.room.BaseDao
import com.example.chatapp.model.pojo.group_chat.GroupMessage

@Dao
interface GroupChatDao: BaseDao<GroupMessage> {

    @Query("Select * from groupmessage where groupId = :groupId")
    suspend fun getGroupMessages(groupId: String): List<GroupMessage>

}