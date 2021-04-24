package com.zancheema.android.telegram.data.source

import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow

interface AppDataSource {
    fun observeUsers(): Flow<Result<List<User>>>

    suspend fun getUsers(): Result<List<User>>

    fun observeUserDetails(): Flow<Result<List<UserDetail>>>

    suspend fun getUserDetails(): Result<List<UserDetail>>

    fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>>

    suspend fun getUserDetails(
        phoneNumbers: List<String>
    ): Result<List<UserDetail>>

    fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>>

    suspend fun getUserDetail(
        phoneNumber: String
    ): Result<UserDetail>

    fun observeChatRooms(): Flow<Result<List<ChatRoom>>>

    suspend fun getChatRooms(): Result<List<ChatRoom>>

    fun observeChatRoom(id: String): Flow<Result<ChatRoom>>

    suspend fun getChatRoom(id: String): Result<ChatRoom>

    fun observeChats(): Flow<Result<List<Chat>>>

    suspend fun getChats(): Result<List<Chat>>

    fun observeChatMessages(): Flow<Result<List<ChatMessage>>>

    suspend fun getChatMessages(): Result<List<ChatMessage>>

    fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>>

    suspend fun getChatMessages(
        chatRoomId: String
    ): Result<List<ChatMessage>>

    fun observeChatMessage(id: String): Flow<Result<ChatMessage>>

    suspend fun getChatMessage(id: String): Result<ChatMessage>

    suspend fun isRegistered(phoneNumber: String): Result<Boolean>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(detail: UserDetail)

    suspend fun saveChatMessage(message: ChatMessage)

    suspend fun saveChatRoom(room: ChatRoom)

    suspend fun deleteAllUsers()

    suspend fun deleteUser(user: User)

    suspend fun deleteUser(phoneNumber: String)

    suspend fun deleteAllChatRooms()

    suspend fun deleteChatRoom(room: ChatRoom)

    suspend fun deleteChatRoom(id: String)

    suspend fun deleteAllChatMessages()

    suspend fun deleteChatMessages(chatRoomId: String)

    suspend fun deleteChatMessage(message: ChatMessage)

    suspend fun deleteChatMessage(id: String)
}