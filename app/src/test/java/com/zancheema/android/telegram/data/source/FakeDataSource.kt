package com.zancheema.android.telegram.data.source

import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class FakeDataSource : AppDataSource {

    private val users = mutableMapOf<String, User>()
    private val userDetails = mutableMapOf<String, UserDetail>()
    private val chatRooms = mutableMapOf<String, ChatRoom>()
    private val chatMessages = mutableMapOf<String, ChatMessage>()

    var returnError = false

    fun setChatRooms(rooms: List<ChatRoom>) {
        chatRooms.clear()
        for (r in rooms) chatRooms[r.id] = r
    }

    fun setChatMessages(messages: List<ChatMessage>) {
        chatMessages.clear()
        for (m in messages) chatMessages[m.id] = m
    }

    override fun observeUsers(): Flow<Result<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Result<List<User>> {
        return Success(users.values.toList())
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(): Result<List<UserDetail>> {
        return Success(userDetails.values.toList())
    }

    override suspend fun getUserDetails(phoneNumbers: List<String>): Result<List<UserDetail>> {
        val details = userDetails.values.toList()
            .filter { phoneNumbers.contains(it.phoneNumber) }
        return Success(details)
    }

    override fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetail(phoneNumber: String): Result<UserDetail> {
        val detail = userDetails[phoneNumber] ?: return getError("UserDetail not found")
        return Success(detail)
    }

    private fun getError(message: String): Error {
        return Error(Exception(message))
    }

    override fun observeChatRooms(): Flow<Result<List<ChatRoom>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRooms(): Result<List<ChatRoom>> {
        return Success(chatRooms.values.toList())
    }

    override fun observeChatRoom(id: String): Flow<Result<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoom(id: String): Result<ChatRoom> {
        val room = chatRooms[id] ?: return getError("ChatRoom not found")
        return Success(room)
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
        return Success(chatMessages.values.toList())
    }

    override suspend fun getChatMessages(chatRoomId: String): Result<List<ChatMessage>> {
        val messages = chatMessages.values.filter { it.chatRoomId == chatRoomId }
        return Success(messages.toList())
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessage(id: String): Result<ChatMessage> {
        val message = chatMessages[id] ?: return getError("ChatMessage not found")
        return Success(message)
    }

    override suspend fun isRegistered(phoneNumber: String): Result<Boolean> {
        val detail = userDetails.values.firstOrNull { it.phoneNumber == phoneNumber }
        return Success(detail != null)
    }

    override suspend fun saveUser(user: User): Result<Boolean> {
        if (returnError) return getError("Error saving User")
        users[user.phoneNumber] = user
        return Success(true)
    }

    override suspend fun saveUserDetail(detail: UserDetail): Result<Boolean> {
        if (returnError) return getError("Error saving UserDetail")
        userDetails[detail.phoneNumber] = detail
        return Success(true)
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        chatMessages[message.id] = message
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        chatRooms[room.id] = room
    }

    override suspend fun deleteAllUsers() {
        users.clear()
        userDetails.clear()
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(phoneNumber: String) {
        users.remove(phoneNumber)
    }

    override suspend fun deleteAllChatRooms() {
        chatRooms.clear()
    }

    override suspend fun deleteChatRoom(room: ChatRoom) {
        deleteChatRoom(room.id)
    }

    override suspend fun deleteChatRoom(id: String) {
        chatRooms.remove(id)
    }

    override suspend fun deleteAllChatMessages() {
        chatMessages.clear()
    }

    override suspend fun deleteChatMessages(chatRoomId: String) {
        val messages = chatMessages.values.toMutableList()
        messages.removeIf { it.chatRoomId == chatRoomId }

        chatMessages.clear()
        for (m in messages) chatMessages[m.id] = m
    }

    override suspend fun deleteChatMessage(message: ChatMessage) {
        deleteChatMessage(message.id)
    }

    override suspend fun deleteChatMessage(id: String) {
        chatMessages.remove(id)
    }
}