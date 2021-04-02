package com.zancheema.android.telegram.data.source.domain

data class Chat(
    val chatRoomId: String,
    val photoUrl: String,
    val userName: String,
    val phoneNumber: String,
    val recentMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
