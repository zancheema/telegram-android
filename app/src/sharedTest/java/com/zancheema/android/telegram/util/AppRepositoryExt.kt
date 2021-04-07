package com.zancheema.android.telegram.util

import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import kotlinx.coroutines.runBlocking

/**
 * Convenience function to avoid the overhead of using
 * [runBlocking] every time to save user
 */
fun AppRepository.saveUserBlocking(user: User) = runBlocking {
    this@saveUserBlocking.saveUser(user)
}

fun AppRepository.saveUserDetailBlocking(detail: UserDetail) = runBlocking {
    this@saveUserDetailBlocking.saveUserDetail(detail)
}

fun AppRepository.saveChatRoomBlocking(room: ChatRoom) = runBlocking {
    this@saveChatRoomBlocking.saveChatRoom(room)
}

fun AppRepository.saveChatMessageBlocking(message: ChatMessage) = runBlocking {
    this@saveChatMessageBlocking.saveChatMessage(message)
}