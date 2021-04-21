package com.zancheema.android.telegram.source

import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.util.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepository @Inject constructor() : AppRepository {

    private val observableUsers = MutableStateFlow<List<User>>(emptyList())
    private var observableUserDetails = MutableStateFlow<List<UserDetail>>(emptyList())
    private val observableChatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    private val observableChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())

    private val usersServiceData = LinkedHashMap<String, User>()
    private val userDetailsServiceData = LinkedHashMap<String, UserDetail>()
    private val chatRoomsServiceData = LinkedHashMap<String, ChatRoom>()
    private val chatMessagesServiceData = LinkedHashMap<String, ChatMessage>()

    private val observableChats = MutableStateFlow<List<Chat>>(emptyList())

    fun setChats(chats: List<Chat>) {
        observableChats.value = chats
    }

    override suspend fun refreshUserDetails() {
        observableUserDetails.value = userDetailsServiceData.values.toList()
    }

    override suspend fun refreshUserDetail(phoneNumber: String) {
        val tmp = observableUserDetails.value.toMutableList()
        tmp.removeIf { it.phoneNumber == phoneNumber }
        userDetailsServiceData[phoneNumber]?.let {
            tmp.add(it)
        }
        observableUserDetails.value = tmp
    }

    override suspend fun refreshChatMessage(id: String) {
        val tmp = observableChatMessages.value.toMutableList()
        tmp.removeIf { it.id == id }
        chatMessagesServiceData[id]?.let {
            tmp.add(it)
        }
        observableChatMessages.value = tmp
    }

    override fun observeUsers(): Flow<Result<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUser(phoneNumber: String) {
        val tmp = observableUsers.value.toMutableList()
        tmp.removeIf { it.phoneNumber == phoneNumber }
        usersServiceData[phoneNumber]?.let {
            tmp.add(it)
        }
        observableUsers.value = tmp
    }

    override suspend fun refreshUsers() {
        observableUsers.value = usersServiceData.values.toList()
    }

    override suspend fun refreshUsers(phoneNumbers: List<String>) {
        val previousUsers = observableUsers.value.toMutableList()
        previousUsers.removeIf { phoneNumbers.contains(it.phoneNumber) }
        val filtered = usersServiceData
            .filterValues { phoneNumbers.contains(it.phoneNumber) }
        previousUsers.addAll(filtered.values)
    }

    override suspend fun refreshUserDetails(phoneNumbers: List<String>) {
        val previousUsers = observableUserDetails.value.toMutableList()
        previousUsers.removeIf { phoneNumbers.contains(it.phoneNumber) }
        val filtered = userDetailsServiceData
            .filterValues { phoneNumbers.contains(it.phoneNumber) }
        previousUsers.addAll(filtered.values)
    }

    override suspend fun deleteUser(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserDetail(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        runBlocking {
            refreshUsers()
            refreshUserDetails()
        }
        return observableUserDetails
            .map { details ->
                val filtered = details.filter { phoneNumbers.contains(it.phoneNumber) }
                Success(filtered)
            }
    }

    override suspend fun getUserDetails(
        phoneNumbers: List<String>,
        forceUpdate: Boolean
    ): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatRooms() {
        observableChatRooms.value = chatRoomsServiceData.values.toList()
    }

    override suspend fun refreshChatRoom(id: String) {
        val tmp = observableChatRooms.value.toMutableList()
        tmp.removeIf { it.id == id }
        chatRoomsServiceData[id]?.let {
            tmp.add(it)
        }
        observableChatRooms.value = tmp
    }

    override suspend fun refreshChats() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatMessages() {
        observableChatMessages.value = chatMessagesServiceData.values.toList()
    }

    override suspend fun refreshChatMessages(chatRoomId: String) {
        val tmp = observableChatMessages.value.toMutableList()
        tmp.removeIf { it.chatRoomId == chatRoomId }
        val newMessages = chatMessagesServiceData.values.filter { it.chatRoomId == chatRoomId }
        tmp.addAll(newMessages)
        observableChatMessages.value = tmp
    }

    override suspend fun getUserDetails(forceUpdate: Boolean): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        runBlocking { refreshUserDetails() }
        return observableUserDetails
            .map { Success(it) }
    }

    override suspend fun getUserDetail(
        phoneNumber: String,
        forceUpdate: Boolean
    ): Result<UserDetail> {
        userDetailsServiceData[phoneNumber]?.let {
            return Success(it)
        } ?: return Error(Exception("UserDetail not found"))
    }

    override fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>> {
        runBlocking { refreshUserDetail(phoneNumber) }
        return observableUserDetails.map { details ->
            val detail = details.firstOrNull()
            if (detail == null) Error(Exception("user detail not found"))
            else Success(detail)
        }
    }

    override suspend fun getChatRooms(forceUpdate: Boolean): Result<List<ChatRoom>> {
        return Success(observableChatRooms.first())
    }

    override suspend fun getChatRoom(id: String, forceUpdate: Boolean): Result<ChatRoom> {
        if (forceUpdate) refreshChatRoom(id)
        val room = observableChatRooms.first().firstOrNull { it.id == id }
            ?: return Error(Exception())
        return Success(room)
    }

    override suspend fun getChats(forceUpdate: Boolean): Result<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(forceUpdate: Boolean): Result<List<ChatMessage>> {
        if (forceUpdate) refreshChatMessages()
        return Success(observableChatMessages.first())
    }

    override suspend fun getChatMessages(
        chatRoomId: String,
        forceUpdate: Boolean
    ): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessage(id: String, forceUpdate: Boolean): Result<ChatMessage> {
        TODO("Not yet implemented")
    }

    override fun observeChatRooms(): Flow<Result<List<ChatRoom>>> {
        TODO("Not yet implemented")
    }

    override fun observeChatRoom(id: String): Flow<Result<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override fun observeChats(): Flow<Result<List<Chat>>> {
        return observableChats.map { Success(it) }
    }

    override fun observeChatMessages(): Flow<Result<List<ChatMessage>>> {
        runBlocking { refreshChatMessages() }
        return observableChatMessages.map { Success(it) }
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
        runBlocking { refreshChatMessages(chatRoomId) }
        return observableChatMessages
            .map { messages ->
                Success(messages.filter { it.chatRoomId == chatRoomId })
            }
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegistered(phoneNumber: String, forceUpdate: Boolean): Result<Boolean> {
        wrapEspressoIdlingResource {
            if (forceUpdate) refreshUserDetail(phoneNumber)
            for (u in observableUserDetails.value) {
                if (u.phoneNumber == phoneNumber) return Success(true)
            }
            return Success(false)
        }
    }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.Main) {
        usersServiceData[user.phoneNumber] = user
    }

    override suspend fun saveUserDetail(detail: UserDetail) = withContext(Dispatchers.Main) {
        userDetailsServiceData[detail.phoneNumber] = detail
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        chatMessagesServiceData[message.id] = message
        refreshChatMessages() // to keep chat messages always realtime
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        chatRoomsServiceData[room.id] = room
    }

    override suspend fun deleteAllUsers() {
        observableUsers.value = emptyList()
    }

    override suspend fun deleteAllUserDetails() {
        observableUserDetails.value = emptyList()
    }

    override suspend fun deleteAllChatRooms() {
        observableChatRooms.value = emptyList()
    }

    override suspend fun deleteChatRoom(room: ChatRoom) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatRoom(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllChatMessages() {
        observableChatMessages.value = emptyList()
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