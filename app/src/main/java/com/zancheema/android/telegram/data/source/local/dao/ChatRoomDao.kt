package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.*
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM chat_rooms")
    suspend fun getAllChatRooms(): List<DbChatRoom>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatRoom(chatRoom: DbChatRoom)

    @Delete
    fun deleteChatRoom(chatRoom: DbChatRoom)

    @Query("DELETE FROM chat_rooms")
    suspend fun deleteAllChatRooms()
}
