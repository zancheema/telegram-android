package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "chat_room_details",
    primaryKeys = ["chat_room_id"],
    foreignKeys = [
        ForeignKey(
            entity = DbChatRoom::class,
            parentColumns = ["id"],
            childColumns = ["chat_room_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class DbChatRoomDetail(
    @ColumnInfo(name = "chat_room_id") val chatRoomId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "recent_message") val recentMessage: String
)
