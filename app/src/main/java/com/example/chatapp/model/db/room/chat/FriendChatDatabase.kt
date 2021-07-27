package com.eduaid.child.models.db.room.chat

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eduaid.child.models.pojo.friend_chat.Message

@Database(entities = [Message::class], version = 1)
abstract class FriendChatDatabase : RoomDatabase() {

    abstract val friendChatDao: FriendChatDao

    companion object {

        @Volatile
        private var instance: FriendChatDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            FriendChatDatabase::class.java, "friend_chat_db"
        ).build()
    }
}