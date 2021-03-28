package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.*
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms")
    suspend fun getAll(): List<DbChatRoom>

    @Query("SELECT * FROM chat_rooms WHERE id = :id")
    suspend fun getById(id: String): DbChatRoom?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chatRoom: DbChatRoom)

    @Query("DELETE FROM chat_rooms")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(chatRoom: DbChatRoom)

    @Query("DELETE FROM chat_rooms WHERE id = :id")
    suspend fun deleteById(id: String)
}