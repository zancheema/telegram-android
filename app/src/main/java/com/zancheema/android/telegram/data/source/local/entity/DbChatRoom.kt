package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "chat_rooms",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = DbUser::class,
            parentColumns = ["phone_number"],
            childColumns = ["phone_number"]
        )
    ]
)
data class DbChatRoom(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String
)
