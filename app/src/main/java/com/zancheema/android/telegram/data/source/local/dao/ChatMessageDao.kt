package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.zancheema.android.telegram.data.source.local.entity.DbChatMessage

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages")
    suspend fun getAll(): List<DbChatMessage>

    @Query("SELECT * FROM chat_messages WHERE chat_room_id = :id")
    suspend fun getByChatRoomId(id: String): List<DbChatMessage>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getById(id: String): DbChatMessage?

    @Insert(onConflict = REPLACE)
    suspend fun insert(message: DbChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()

    @Query("DELETE FROM chat_messages WHERE chat_room_id = :id")
    suspend fun deleteByChatRoomId(id: String)

    @Delete
    suspend fun delete(message: DbChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteById(id: String)
}