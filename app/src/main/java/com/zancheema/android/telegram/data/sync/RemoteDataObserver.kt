package com.zancheema.android.telegram.data.sync

import com.zancheema.android.telegram.data.source.remote.dto.ChatMessageDTO
import com.zancheema.android.telegram.data.source.remote.dto.ChatRoomDTO
import com.zancheema.android.telegram.data.source.remote.dto.UserDTO

interface RemoteDataObserver {
    fun observeUsers(onChange: (List<UserDTO>) -> Unit = {})

    fun observeChatRooms(onChange: (List<ChatRoomDTO>) -> Unit = {})

    fun observeChatMessages(chatRoomId: String, onChange: (List<ChatMessageDTO>) -> Unit = {})
}