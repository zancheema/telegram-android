package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "chat_rooms",
    primaryKeys = ["id"]
)
data class DbChatRoom(
    @ColumnInfo(name = "id") val id: String
)
