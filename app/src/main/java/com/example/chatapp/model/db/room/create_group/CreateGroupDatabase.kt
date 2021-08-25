package com.example.chatapp.model.db.room.create_group

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chatapp.model.pojo.create_group.Group

@Database(entities = [Group::class], version = 1)
abstract class CreateGroupDatabase: RoomDatabase(){
    abstract val createGroupDao: CreateGroupDao

    companion object {

        @Volatile
        private var instance: CreateGroupDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            CreateGroupDatabase::class.java, "create_group_db"
        ).build()
    }
}