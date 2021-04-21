package com.zancheema.android.telegram.data.source.local

import com.zancheema.android.telegram.data.source.AppDataSource
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.data.source.local.entity.asDomainModel
import com.zancheema.android.telegram.data.source.local.view.asDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppDataSource {
    override fun observeUsers(): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): List<User> {
        return database.userDao().getAll()
            .map { it.asDomainModel() }
    }

    override fun observeUserDetails(): Flow<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(): List<UserDetail> {
        return database.userDetailDao().getAll()
            .map { it.asDomainModel() }
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(
        phoneNumbers: List<String>
    ): List<UserDetail> {
        return database.userDetailDao()
            .getUserDetailsByPhoneNumbers(phoneNumbers)
            .map { it.asDomainModel() }
    }

    override fun observeUserDetail(phoneNumber: String): Flow<UserDetail?> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetail(phoneNumber: String): UserDetail? {
        return database.userDetailDao()
            .getUserDetailByPhoneNumber(phoneNumber)
            ?.asDomainModel()
    }

    override fun observeChatRooms(): Flow<List<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRooms(): List<ChatRoom> {
        return database.chatRoomDao()
            .getAll()
            .map { it.asDomainModel() }
    }

    override fun observeChatRoom(id: String): Flow<ChatRoom?> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoom(id: String): ChatRoom? {
        return database.chatRoomDao()
            .getById(id)
            ?.asDomainModel()
    }

    override fun observeChats(): Flow<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChats(): List<Chat> {
        return database.chatRoomDao()
            .getAllChats()
            .map { it.asDomainModel() }
    }

    override fun observeChatMessages(): Flow<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(): List<ChatMessage> {
        return database.chatMessageDao()
            .getAll()
            .map { it.asDomainModel() }
    }

    override fun observeChatMessages(chatRoomId: String): Flow<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(
        chatRoomId: String
    ): List<ChatMessage> {
        return database.chatMessageDao()
            .getByChatRoomId(chatRoomId)
            .map { it.asDomainModel() }
    }

    override fun observeChatMessage(id: String): Flow<ChatMessage?> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessage(id: String): ChatMessage? {
        return database.chatMessageDao()
            .getById(id)
            ?.asDomainModel()
    }

    override suspend fun isRegistered(phoneNumber: String): Boolean {
        return database.userDetailDao().getUserDetailByPhoneNumber(phoneNumber) != null
    }

    override suspend fun saveUser(user: User) = withContext(ioDispatcher) {
        database.userDao().insertUser(user.asDatabaseEntity())
    }

    override suspend fun saveUserDetail(detail: UserDetail) = withContext(ioDispatcher) {
        database.userDetailDao().insertUserDetail(detail.asDatabaseEntity())
    }

    override suspend fun saveChatMessage(message: ChatMessage) = withContext(ioDispatcher) {
        database.chatMessageDao().insert(message.asDatabaseEntity())
    }

    override suspend fun saveChatRoom(room: ChatRoom) = withContext(ioDispatcher) {
        database.chatRoomDao().insert(room.asDatabaseEntity())
    }

    override suspend fun deleteAllUsers() = withContext(ioDispatcher) {
        database.userDao().deleteAll()
    }

    override suspend fun deleteUser(user: User) = withContext(ioDispatcher) {
        database.userDao().deleteUser(user.asDatabaseEntity())
    }

    override suspend fun deleteUser(phoneNumber: String) = withContext(ioDispatcher) {
        database.userDao().deleteUserByPhoneNumber(phoneNumber)
    }

    override suspend fun deleteAllChatRooms() = withContext(ioDispatcher) {
        database.chatRoomDao().deleteAll()
    }

    override suspend fun deleteChatRoom(room: ChatRoom) = withContext(ioDispatcher) {
        database.chatRoomDao().delete(room.asDatabaseEntity())
    }

    override suspend fun deleteChatRoom(id: String) = withContext(ioDispatcher) {
        database.chatRoomDao().deleteById(id)
    }

    override suspend fun deleteAllChatMessages() = withContext(ioDispatcher) {
        database.chatMessageDao().deleteAll()
    }

    override suspend fun deleteChatMessages(chatRoomId: String) = withContext(ioDispatcher) {
        database.chatMessageDao().deleteByChatRoomId(chatRoomId)
    }

    override suspend fun deleteChatMessage(message: ChatMessage) = withContext(ioDispatcher) {
        database.chatMessageDao().delete(message.asDatabaseEntity())
    }

    override suspend fun deleteChatMessage(id: String) = withContext(ioDispatcher) {
        database.chatMessageDao().deleteById(id)
    }
}
