package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "chat_messages",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbChatRoom::class,
            parentColumns = ["id"],
            childColumns = ["chat_room_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = DbUser::class,
            parentColumns = ["phone_number"],
            childColumns = ["sender"]
        ),
        ForeignKey(
            entity = DbUser::class,
            parentColumns = ["phone_number"],
            childColumns = ["receiver"]
        )
    ]
)
data class DbChatMessage(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "chat_room_id") val chatRoomId: String,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "receiver") val receiver: String,
    @ColumnInfo(name = "time_sent") val timeSent: Long,
    @ColumnInfo(name = "message") val message: String
)
