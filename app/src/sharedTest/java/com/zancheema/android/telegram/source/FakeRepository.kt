package com.zancheema.android.telegram.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.util.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepository @Inject constructor() : AppRepository {

    private val observableUsers = MutableLiveData<List<User>>(emptyList())
    private var observableUserDetails = MutableStateFlow<List<UserDetail>>(emptyList())
    private val observableChatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    private val observableChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    override suspend fun refreshUserDetails() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUserDetail(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatMessage(id: String) {
        TODO("Not yet implemented")
    }

    override fun observeUsers(): Flow<Result<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUsers(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserDetail(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatRooms() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatRoom(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChats() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatMessages() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshChatMessages(chatRoomId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(forceUpdate: Boolean): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        return observableUserDetails
            .map { Success(it) }
    }

    override suspend fun getUserDetail(phoneNumber: String, forceUpdate: Boolean): Result<UserDetail> {
        val detail = observableUserDetails.value.firstOrNull()
            ?: return Error(Exception("UserDetail not found"))
        return Success(detail)
    }

    override fun observeUserDetail(phoneNumber: String): LiveData<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRooms(forceUpdate: Boolean): Result<List<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoom(id: String, forceUpdate: Boolean): Result<ChatRoom> {
        TODO("Not yet implemented")
    }

    override suspend fun getChats(forceUpdate: Boolean): Result<List<Chat>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(forceUpdate: Boolean): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(chatRoomId: String, forceUpdate: Boolean): Result<List<ChatMessage>> {
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
        TODO("Not yet implemented")
    }

    override fun observeChatMessages(): Flow<Result<List<ChatMessage>>> {
        return observableChatMessages.map { Success(it) }
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
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
            for (u in observableUsers.value!!) {
                if (u.phoneNumber == phoneNumber) return Success(true)
            }
            return Success(false)
        }
    }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.Main) {
        observableUsers.value = observableUsers.value!!.toMutableList().apply { add(user) }
    }

    override suspend fun saveUserDetail(detail: UserDetail) = withContext(Dispatchers.Main) {
        val tmp = observableUserDetails.value.toMutableList()
        observableUsers.value?.let {
            it.firstOrNull() ?: error("User does not exist for this detail")
        } ?: error("User does not exist for this detail")
        tmp.add(detail)
        observableUserDetails.value = tmp
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        val tmp = observableChatMessages.value.toMutableList()
        tmp.add(message)
        observableChatMessages.value = tmp
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        val tmp = observableChatRooms.value.toMutableList()
        tmp.add(room)
        observableChatRooms.value = tmp
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