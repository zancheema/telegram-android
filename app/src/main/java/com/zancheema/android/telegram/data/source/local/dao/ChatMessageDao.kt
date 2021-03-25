package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zancheema.android.telegram.data.source.local.entity.DbChatMessage

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE id = :id")
    suspend fun getChatMessageById(id: String): DbChatMessage?

    @Query("SELECT * FROM chat_messages WHERE chat_room_id = :chatRoomId")
    suspend fun getChatMessagesByChatRoomId(chatRoomId: String): List<DbChatMessage>

    @Insert
    suspend fun insertChatMessage(message: DbChatMessage)

    @Delete
    suspend fun deleteChatMessage(message: DbChatMessage)

    @Query("DELETE FROM chat_messages WHERE chat_room_id = :chatRoomId")
    suspend fun deleteChatMessagesByChatRoomId(chatRoomId: String)
}
