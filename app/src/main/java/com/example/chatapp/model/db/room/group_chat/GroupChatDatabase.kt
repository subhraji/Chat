package com.example.chatapp.model.db.room.group_chat

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eduaid.child.models.pojo.friend_chat.Message
import com.example.chatapp.model.pojo.group_chat.GroupMessage

@Database(entities = [GroupMessage::class], version = 1)
abstract class GroupChatDatabase : RoomDatabase() {
    abstract val groupChatDao: GroupChatDao

    companion object {

        @Volatile
        private var instance: GroupChatDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            GroupChatDatabase::class.java, "group_chat_db"
        ).build()
    }
}