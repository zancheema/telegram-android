package com.zancheema.android.telegram.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.util.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepository @Inject constructor() : AppRepository {

    private val observableUser = MutableLiveData<User>(null)
    private val observableUsers = MutableLiveData<List<User>>(emptyList())

    private var observableUserDetails = MutableStateFlow<List<UserDetail>>(emptyList())
    private val observableChatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    private val observableChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())

    override suspend fun getAllUserDetails(): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeAllUserDetails(): Flow<Result<List<UserDetail>>> {
        return observableUserDetails
            .map { Success(it) }
    }

    override suspend fun getUserDetail(): Result<UserDetail> {
        TODO("Not yet implemented")
//        return observableUserDetail.value?.let {
//            Success(it)
//        } ?: Error(Exception("user does not exist"))
    }

    override fun observeUserDetail(): LiveData<Result<UserDetail>> {
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
        return observableChatMessages.map { Success(it) }
    }

    override fun observeChatMessagesByChatRoomId(id: String): Flow<Result<List<ChatMessage>>> {
        return observableChatMessages
            .map { messages ->
                Success(messages.filter { it.chatRoomId == id })
            }
    }

    override fun observeChatMessageById(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean> {
        wrapEspressoIdlingResource {
            for (u in observableUsers.value!!) {
                if (u.phoneNumber == phoneNumber) return Success(true)
            }
            return Success(false)
        }
    }

    override fun observeUserExists(): LiveData<Result<Boolean>> =
        observableUser.map { Success(it != null) }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.Main) {
        // save user
        observableUser.value = user
        observableUsers.value = observableUsers.value!!.toMutableList().apply { add(user) }
    }

    override suspend fun saveUserDetail(detail: UserDetail) = withContext(Dispatchers.Main) {
        val tmp = observableUserDetails.value.toMutableList()
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

    override suspend fun deleteChatRoomById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllChatMessages() {
        observableChatMessages.value = emptyList()
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
}