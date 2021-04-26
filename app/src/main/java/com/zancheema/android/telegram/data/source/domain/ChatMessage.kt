package com.zancheema.android.telegram.data.source.domain

import com.zancheema.android.telegram.data.source.local.entity.DbChatMessage
import com.zancheema.android.telegram.data.source.remote.dto.ChatMessageDTO

data class ChatMessage(
    val id: String,
    val chatRoomId: String,
    val message: String,
    val isMine: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

fun ChatMessage.asDatabaseEntity() = DbChatMessage(
    id = id,
    chatRoomId = chatRoomId,
    message = message,
    isMine = isMine,
    timestamp = timestamp
)

fun ChatMessage.asDataTransferObject() = ChatMessageDTO(
    id = id,
    chatRoomId = chatRoomId,
    message = message,
    mine = isMine,
    timestamp = timestamp
)
