package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun getAllUserDetails(): Result<List<UserDetail>>

    fun observeAllUserDetails(): Flow<Result<List<UserDetail>>>

    suspend fun getUserDetail(): Result<UserDetail>

    fun observeUserDetail(): LiveData<Result<UserDetail>>

    suspend fun getAllChatRooms(): Result<List<ChatRoom>>

    suspend fun getChatRoomById(id: String): Result<ChatRoom>

    suspend fun getAllChats(): Result<List<Chat>>

    suspend fun getAllChatMessages(): Result<List<ChatMessage>>

    suspend fun getChatMessagesByChatRoomId(id: String): Result<List<ChatMessage>>

    suspend fun getChatMessageById(id: String): Result<ChatMessage>

    fun observeAllChatRooms(): Flow<Result<List<ChatRoom>>>

    fun observeChatRoomById(id: String): Flow<Result<ChatRoom>>

    fun observeAllChats(): Flow<Result<List<Chat>>>

    fun observeAllChatMessages(): Flow<Result<List<ChatMessage>>>

    fun observeChatMessagesByChatRoomId(id: String): Flow<Result<List<ChatMessage>>>

    fun observeChatMessageById(id: String): Flow<Result<ChatMessage>>

    suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean>

    fun observeUserExists(): LiveData<Result<Boolean>>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(detail: UserDetail)

    suspend fun saveChatMessage(message: ChatMessage)

    suspend fun saveChatRoom(room: ChatRoom)

    suspend fun deleteAllUsers()

    suspend fun deleteAllUserDetails()

    suspend fun deleteAllChatRooms()

    suspend fun deleteChatRoom(room: ChatRoom)

    suspend fun deleteChatRoomById(id: String)

    suspend fun deleteAllChatMessages()

    suspend fun deleteChatMessagesByChatRoomId(id: String)

    suspend fun deleteChatMessage(message: ChatMessage)

    suspend fun deleteChatMessageById(id: String)
}