package com.zancheema.android.telegram.data.source.domain

data class ChatMessage(
    val id: String,
    val chatRoomId: String,
    val message: String,
    val isMine: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
