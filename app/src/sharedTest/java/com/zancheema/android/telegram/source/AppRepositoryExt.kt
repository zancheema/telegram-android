package com.zancheema.android.telegram.source

import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.ChatMessage
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

fun AppRepository.saveMessageBlocking(message: ChatMessage) = runBlocking {
    this@saveMessageBlocking.saveMessage(message)
}