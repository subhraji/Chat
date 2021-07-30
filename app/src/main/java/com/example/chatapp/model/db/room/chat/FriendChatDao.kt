package com.eduaid.child.models.db.room.chat
import androidx.room.Dao
import androidx.room.Query
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.model.db.room.BaseDao

@Dao
interface FriendChatDao: BaseDao<Message> {

    @Query("Select * from message where userId = :friendId order by createdAt")
    suspend fun getChatMessages(friendId: String): List<Message>

    /*@Query("Update message set isSent=:isSent where msgUuid =:msgUuid")
    suspend fun updateIsSentStatus(isSent: Boolean, msgUuid: String)*/

    /*@Query("Select * from message where isSent=:isSent")
    suspend fun getUnsentMessages(isSent:Boolean): List<Message>*/

    @Query("Select count(*) from message where userId=:friendId")
    suspend fun getChatMessageCount(friendId: String): Int
}