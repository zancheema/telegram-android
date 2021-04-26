package com.zancheema.android.telegram.data.source.remote.dto

import com.zancheema.android.telegram.data.source.domain.ChatMessage

data class ChatMessageDTO(
    val id: String = "",
    val chatRoomId: String = "",
    val message: String = "",
    val mine: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

fun ChatMessageDTO.asDomainModel() = ChatMessage(
    id = id,
    chatRoomId = chatRoomId,
    message = message,
    isMine = mine,
    timestamp = timestamp
)
