package com.zancheema.android.telegram.data.source.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    value = """
        SELECT 
            cr.id as chat_room_id,
            ud.photo_url AS photo_url,
            -- CONCAT(ud.first_name, " ", ud.last_name) AS user_name,
            ud.first_name AS user_name,
            u.phone_number AS phone_number,
            cm.message as recent_message,
            cm.timestamp as timestamp
        FROM chat_rooms cr
        JOIN users u ON u.phone_number = cr.phone_number
        JOIN user_details ud ON ud.phone_number = u.phone_number
        JOIN (SELECT * FROM chat_messages ORDER BY timestamp) cm ON cr.id = cm.chat_room_id
        GROUP BY cm.chat_room_id
        ORDER BY cm.timestamp DESC
    """,
    viewName = "chats"
)
data class DbChat(
    @ColumnInfo(name = "chat_room_id") val chatRoomId: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "recent_message") val recentMessage: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
