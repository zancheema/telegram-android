package com.zancheema.android.telegram.data.source

import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor() : AppDataSource {
    override fun observeUsers(): Flow<Result<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(phoneNumbers: List<String>): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetail(phoneNumber: String): Result<UserDetail> {
        TODO("Not yet implemented")
    }

    override fun observeChatRooms(): Flow<Result<List<ChatRoom>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRooms(): Result<List<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override fun observeChatRoom(id: String): Flow<Result<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoom(id: String): Result<ChatRoom> {
        TODO("Not yet implemented")
    }

    override fun observeChats(): Flow<Result<List<Chat>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChats(): Result<List<Chat>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessages(): Flow<Result<List<ChatMessage>>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(chatRoomId: String): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessage(id: String): Result<ChatMessage> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegistered(phoneNumber: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUser(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserDetail(detail: UserDetail): Result<Boolean> {
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

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllChatRooms() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatRoom(room: ChatRoom) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatRoom(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllChatMessages() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatMessages(chatRoomId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatMessage(message: ChatMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatMessage(id: String) {
        TODO("Not yet implemented")
    }
}