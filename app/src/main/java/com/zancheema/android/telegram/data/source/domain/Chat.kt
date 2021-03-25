package com.zancheema.android.telegram.data.source.domain

import java.net.URI
import java.time.LocalDateTime

data class Chat(
    val name: String,
    val phoneNumber: String,
    val recentMessage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: URI = URI("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png")
)