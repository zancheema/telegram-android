package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.zancheema.android.telegram.data.source.local.entity.DbChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<DbChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    suspend fun getAll(): List<DbChatMessage>

    @Query("SELECT * FROM chat_messages WHERE chat_room_id = :id ORDER BY timestamp DESC")
    fun observeByChatRoomId(id: String): Flow<List<DbChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE chat_room_id = :id ORDER BY timestamp DESC")
    suspend fun getByChatRoomId(id: String): List<DbChatMessage>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    fun observeById(id: String): Flow<DbChatMessage?>

    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getById(id: String): DbChatMessage?

    @Insert(onConflict = IGNORE)
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