package com.zancheema.android.telegram.data.source.domain

import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import com.zancheema.android.telegram.data.source.remote.dto.ChatRoomDTO

data class ChatRoom(
    val id: String,
    val phoneNumber: String
)

fun ChatRoom.asDatabaseEntity() = DbChatRoom(
    id = id,
    phoneNumber = phoneNumber
)

fun ChatRoom.asDataTransferObject() = ChatRoomDTO(
    id = id,
    phoneNumber = phoneNumber
)