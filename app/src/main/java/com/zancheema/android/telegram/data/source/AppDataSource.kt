package com.zancheema.android.telegram.data.source

import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow

interface AppDataSource {
    fun observeUsers(): Flow<List<User>>

    suspend fun getUsers(): List<User>

    fun observeUserDetails(): Flow<List<UserDetail>>

    suspend fun getUserDetails(): List<UserDetail>

    fun observeUserDetails(phoneNumbers: List<String>): Flow<List<UserDetail>>

    suspend fun getUserDetails(
        phoneNumbers: List<String>
    ): List<UserDetail>

    fun observeUserDetail(phoneNumber: String): Flow<UserDetail?>

    suspend fun getUserDetail(
        phoneNumber: String
    ): UserDetail?

    fun observeChatRooms(): Flow<List<ChatRoom>>

    suspend fun getChatRooms(): List<ChatRoom>

    fun observeChatRoom(id: String): Flow<ChatRoom?>

    suspend fun getChatRoom(id: String): ChatRoom?

    fun observeChats(): Flow<List<Chat>>

    suspend fun getChats(): List<Chat>

    fun observeChatMessages(): Flow<List<ChatMessage>>

    suspend fun getChatMessages(): List<ChatMessage>

    fun observeChatMessages(chatRoomId: String): Flow<List<ChatMessage>>

    suspend fun getChatMessages(
        chatRoomId: String
    ): List<ChatMessage>

    fun observeChatMessage(id: String): Flow<ChatMessage?>

    suspend fun getChatMessage(id: String): ChatMessage?

    suspend fun isRegistered(phoneNumber: String): Boolean

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