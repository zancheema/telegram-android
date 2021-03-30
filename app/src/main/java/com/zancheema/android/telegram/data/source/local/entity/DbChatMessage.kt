package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "chat_messages",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbChatRoom::class,
            parentColumns = ["id"],
            childColumns = ["chat_room_id"],
            onDelete = CASCADE
        )
    ]
)
data class DbChatMessage(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "chat_room_id") val chatRoomId: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "is_mine") val isMine: Boolean = true,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)
