package com.example.chatapp.model.db.room.chat_user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatapp.model.pojo.chat_user.ChatUser

@Database(entities = [ChatUser::class], version = 1)
abstract class ChatUserDatabase: RoomDatabase() {

    abstract val chatUserDao: ChatUserDao

    companion object {

        @Volatile
        private var instance: ChatUserDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ChatUserDatabase::class.java, "chat_user_db"
        ).build()
    }
}