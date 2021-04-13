package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun observeUsers(): Flow<Result<List<User>>>

    suspend fun getUsers(): Result<List<User>>

    suspend fun refreshUsers(phoneNumber: String)

    fun observeUserDetails(): Flow<Result<List<UserDetail>>>

    suspend fun getUserDetails(forceUpdate: Boolean = false): Result<List<UserDetail>>

    suspend fun refreshUserDetails()

    fun observeUserDetail(phoneNumber: String): LiveData<Result<UserDetail>>

    suspend fun getUserDetail(
        phoneNumber: String,
        forceUpdate: Boolean = false
    ): Result<UserDetail>

    suspend fun refreshUserDetail(phoneNumber: String)

    fun observeChatRooms(): Flow<Result<List<ChatRoom>>>

    suspend fun getChatRooms(forceUpdate: Boolean = false): Result<List<ChatRoom>>

    suspend fun refreshChatRooms()

    fun observeChatRoom(id: String): Flow<Result<ChatRoom>>

    suspend fun getChatRoom(id: String, forceUpdate: Boolean = false): Result<ChatRoom>

    suspend fun refreshChatRoom(id: String)

    fun observeChats(): Flow<Result<List<Chat>>>

    suspend fun getChats(forceUpdate: Boolean = false): Result<List<Chat>>

    suspend fun refreshChats()

    fun observeChatMessages(): Flow<Result<List<ChatMessage>>>

    suspend fun getChatMessages(forceUpdate: Boolean = false): Result<List<ChatMessage>>

    suspend fun refreshChatMessages()

    fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>>

    suspend fun getChatMessages(
        chatRoomId: String,
        forceUpdate: Boolean = false
    ): Result<List<ChatMessage>>

    suspend fun refreshChatMessages(chatRoomId: String)

    fun observeChatMessage(id: String): Flow<Result<ChatMessage>>

    suspend fun getChatMessage(id: String, forceUpdate: Boolean = false): Result<ChatMessage>

    suspend fun refreshChatMessage(id: String)

    suspend fun isRegistered(phoneNumber: String, forceUpdate: Boolean = false): Result<Boolean>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(detail: UserDetail)

    suspend fun saveChatMessage(message: ChatMessage)

    suspend fun saveChatRoom(room: ChatRoom)

    suspend fun deleteAllUsers()

    suspend fun deleteUser(phoneNumber: String)

    suspend fun deleteAllUserDetails()

    suspend fun deleteUserDetail(phoneNumber: String)

    suspend fun deleteAllChatRooms()

    suspend fun deleteChatRoom(room: ChatRoom)

    suspend fun deleteChatRoom(id: String)

    suspend fun deleteAllChatMessages()

    suspend fun deleteChatMessages(chatRoomId: String)

    suspend fun deleteChatMessage(message: ChatMessage)

    suspend fun deleteChatMessage(id: String)
}