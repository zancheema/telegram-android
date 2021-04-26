package com.zancheema.android.telegram.data.source.remote.dto

import com.zancheema.android.telegram.data.source.domain.ChatRoom

data class ChatRoomDTO(
    val id: String = "",
    val phoneNumber: String = ""
)

fun ChatRoomDTO.asDomainModel() = ChatRoom(
    id = id,
    phoneNumber = phoneNumber
)