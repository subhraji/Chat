package com.example.chatapp.model.db.room.create_group

import androidx.room.Dao
import androidx.room.Query
import com.example.chatapp.model.db.room.BaseDao
import com.example.chatapp.model.pojo.create_group.Group

@Dao
interface CreateGroupDao: BaseDao<Group> {

    @Query("Select * from `group`")
    suspend fun getGroups(): List<Group>
}