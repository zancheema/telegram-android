package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoomMember

@Dao
interface ChatRoomMemberDao {
    @Query("SELECT * FROM chat_room_members WHERE chat_room_id = :chatRoomId")
    suspend fun getChatRoomMembersByChatRoomId(chatRoomId: String): List<DbChatRoomMember>

    @Query("SELECT * FROM chat_room_members WHERE phone_number = :phoneNumber")
    suspend fun getChatRoomMembersByPhoneNumber(phoneNumber: String): List<DbChatRoomMember>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatRoomMember(member: DbChatRoomMember)
}