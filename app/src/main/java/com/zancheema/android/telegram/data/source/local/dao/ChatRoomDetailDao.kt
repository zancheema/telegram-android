package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoomDetail

@Dao
interface ChatRoomDetailDao {
    @Query("SELECT * FROM chat_room_details WHERE chat_room_id = :chatRoomId")
    suspend fun getChatRoomDetailByChatRoomId(chatRoomId: String): DbChatRoomDetail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoomDetail(detail: DbChatRoomDetail)

    @Query("UPDATE chat_room_details SET recent_message = :recentMessage WHERE chat_room_id = :chatRoomId")
    suspend fun updateRecentMessage(chatRoomId: String, recentMessage: String)
}
