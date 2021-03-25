package com.zancheema.android.telegram.data.source.domain

import java.util.UUID.randomUUID

data class ChatMessage(
    val message: String,
    val from: UserDetail,
    val to: UserDetail,
    val id: String = randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val isMine: Boolean = false
)
