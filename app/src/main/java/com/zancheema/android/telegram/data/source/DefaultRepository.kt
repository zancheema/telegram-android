package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow

class DefaultRepository : AppRepository {
    override suspend fun getAllUserDetails(): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeAllUserDetails(): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetailByPhoneNumber(phoneNumber: String): Result<UserDetail> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetailByPhoneNumber(phoneNumber: String): LiveData<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChatRooms(): Result<List<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoomById(id: String): Result<ChatRoom> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChats(): Result<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllChatMessages(): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessagesByChatRoomId(id: String): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessageById(id: String): Result<ChatMessage> {
        TODO("Not yet implemented")
    }

    override fun observeAllChatRooms(): Flow<Result<List<ChatRoom>>> {
        TODO("Not yet implemented")
    }

    override fun observeChatRoomById(id: String): Flow<Result<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override fun observeAllChats(): Flow<Result<List<Chat>>> {
        TODO("Not yet implemented")
    }

    override fun observeAllChatMessages(): Flow<Result<List<ChatMessage>>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessagesByChatRoomId(id: String): Flow<Result<List<ChatMessage>>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessageById(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserDetail(detail: UserDetail) {
        TODO("Not yet implemented")
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllUsers() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllUserDetails() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllChatRooms() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatRoom(room: ChatRoom) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatRoomById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllChatMessages() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatMessagesByChatRoomId(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatMessage(message: ChatMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatMessageById(id: String) {
        TODO("Not yet implemented")
    }

    override fun observeUserExists(): LiveData<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUser(user: User) {
        TODO("Not yet implemented")
    }
}