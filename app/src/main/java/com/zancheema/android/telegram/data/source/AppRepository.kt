package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.UserDetail

interface AppRepository {
    suspend fun getUserDetail(): Result<UserDetail>

    fun observeUserDetail(): LiveData<Result<UserDetail>>

    suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean>

    suspend fun getChats(): Result<List<Chat>>

    fun observeChats(): LiveData<Result<List<Chat>>>

    suspend fun getMessages(phoneNumber: String): Result<List<ChatMessage>>

    fun observeMessages(phoneNumber: String): LiveData<Result<List<ChatMessage>>>

    fun observeUserExists(): LiveData<Result<Boolean>>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(detail: UserDetail)

    suspend fun saveMessage(message: ChatMessage)
}