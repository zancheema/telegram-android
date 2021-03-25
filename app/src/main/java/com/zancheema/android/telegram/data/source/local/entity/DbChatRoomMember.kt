package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "chat_room_members",
    primaryKeys = ["phone_number", "chat_room_id"],
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
            childColumns = ["phone_number"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class DbChatRoomMember(
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "chat_room_id") val chatRoomId: String
)
