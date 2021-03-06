package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.*
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import com.zancheema.android.telegram.data.source.local.view.DbChat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms")
    fun observeAll(): Flow<List<DbChatRoom>>

    @Query("SELECT * FROM chat_rooms")
    suspend fun getAll(): List<DbChatRoom>

    @Query("SELECT * FROM chat_rooms WHERE id = :id")
    fun observeById(id: String): Flow<DbChatRoom?>

    @Query("SELECT * FROM chat_rooms WHERE id = :id")
    suspend fun getById(id: String): DbChatRoom?

    @Query("SELECT * FROM chats")
    fun observeAllChats(): Flow<List<DbChat>>

    @Query("SELECT * FROM chats")
    suspend fun getAllChats(): List<DbChat>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chatRoom: DbChatRoom)

    @Query("DELETE FROM chat_rooms")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(chatRoom: DbChatRoom)

    @Query("DELETE FROM chat_rooms WHERE id = :id")
    suspend fun deleteById(id: String)
}