package com.zancheema.android.telegram.data.source

import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultRepository(
    private val remoteDataSource: AppDataSource,
    private val localDataSource: AppDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppRepository {

    override fun observeUsers(): Flow<Result<List<User>>> {
        return localDataSource.observeUsers()
    }

    override suspend fun getUsers(): Result<List<User>> {
        return localDataSource.getUsers()
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        return localDataSource.observeUserDetails()
    }

    override suspend fun getUserDetails(forceUpdate: Boolean): Result<List<UserDetail>> {
        if (forceUpdate) {
            try {
                refreshUserDetails()
            } catch (e: Exception) {
                return Error(e)
            }
        }
        return localDataSource.getUserDetails()
    }

    override suspend fun refreshUserDetails() {
        val phoneNumbers = localDataSource.getUsers()
        if (phoneNumbers is Success) {
            val userDetails = remoteDataSource
                .getUserDetails(phoneNumbers.data.map { it.phoneNumber })
            if (userDetails is Success) {
                localDataSource.deleteAllUsers()

                for (detail in userDetails.data) {
                    localDataSource.saveUser(User(detail.phoneNumber))
                    localDataSource.saveUserDetail(detail)
                }
            } else if (userDetails is Error) {
                throw userDetails.exception
            }
        } else if (phoneNumbers is Error) {
            throw phoneNumbers.exception
        }
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        return localDataSource.observeUserDetails(phoneNumbers)
    }

    override suspend fun getUserDetails(
        phoneNumbers: List<String>,
        forceUpdate: Boolean
    ): Result<List<UserDetail>> {
        if (forceUpdate) refreshUserDetails(phoneNumbers)
        return localDataSource.getUserDetails(phoneNumbers)
    }

    override suspend fun refreshUserDetails(phoneNumbers: List<String>) {
        withContext(ioDispatcher) {
            val details = remoteDataSource.getUserDetails(phoneNumbers)
            if (details is Success) {
                for (d in details.data) localDataSource.saveUserDetail(d)
            }
        }
    }

    override fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>> {
        return localDataSource.observeUserDetail(phoneNumber)
    }

    override suspend fun getUserDetail(
        phoneNumber: String,
        forceUpdate: Boolean
    ): Result<UserDetail> {
        if (forceUpdate) refreshUserDetail(phoneNumber)
        return localDataSource.getUserDetail(phoneNumber)
    }

    override suspend fun refreshUserDetail(phoneNumber: String) {
        withContext(ioDispatcher) {
            val userDetail = remoteDataSource.getUserDetail(phoneNumber)
            if (userDetail is Success) {
                localDataSource.saveUserDetail(userDetail.data)
            }
        }
    }

    override fun observeChatRooms(): Flow<Result<List<ChatRoom>>> {
        return localDataSource.observeChatRooms()
    }

    override suspend fun getChatRooms(forceUpdate: Boolean): Result<List<ChatRoom>> {
        if (forceUpdate) refreshChatRooms()
        return localDataSource.getChatRooms()
    }

    override suspend fun refreshChatRooms() {
        withContext(ioDispatcher) {
            val rooms = remoteDataSource.getChatRooms()
            if (rooms is Success) {
                localDataSource.deleteAllChatRooms()
                for (r in rooms.data) localDataSource.saveChatRoom(r)
            }
        }
    }

    override fun observeChatRoom(id: String): Flow<Result<ChatRoom>> {
        return localDataSource.observeChatRoom(id)
    }

    override suspend fun getChatRoom(id: String, forceUpdate: Boolean): Result<ChatRoom> {
        if (forceUpdate) refreshChatRoom(id)
        return localDataSource.getChatRoom(id)
    }

    override suspend fun refreshChatRoom(id: String) {
        withContext(ioDispatcher) {
            val room = remoteDataSource.getChatRoom(id)
            if (room is Success) {
                localDataSource.saveChatRoom(room.data)
            }
        }
    }

    override fun observeChats(): Flow<Result<List<Chat>>> {
        return localDataSource.observeChats()
    }

    override suspend fun getChats(forceUpdate: Boolean): Result<List<Chat>> {
        // TODO: not tested yet
        if (forceUpdate) refreshChats()
        return localDataSource.getChats()
    }

    override suspend fun refreshChats() {
        withContext(ioDispatcher) {
            refreshUserDetails()
            refreshChatRooms()
            refreshChatMessages()
        }
    }

    override fun observeChatMessages(): Flow<Result<List<ChatMessage>>> {
        return localDataSource.observeChatMessages()
    }

    override suspend fun getChatMessages(forceUpdate: Boolean): Result<List<ChatMessage>> {
        if (forceUpdate) refreshChatMessages()
        return localDataSource.getChatMessages()
    }

    override suspend fun refreshChatMessages() {
        withContext(ioDispatcher) {
            val messages = remoteDataSource.getChatMessages()
            if (messages is Success) {
                localDataSource.deleteAllChatMessages()
                for (m in messages.data) localDataSource.saveChatMessage(m)
            }
        }
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
        return localDataSource.observeChatMessages(chatRoomId)
    }

    override suspend fun getChatMessages(
        chatRoomId: String,
        forceUpdate: Boolean
    ): Result<List<ChatMessage>> {
        if (forceUpdate) refreshChatMessages(chatRoomId)
        return localDataSource.getChatMessages(chatRoomId)
    }

    override suspend fun refreshChatMessages(chatRoomId: String) {
        withContext(ioDispatcher) {
            val messages = remoteDataSource.getChatMessages(chatRoomId)
            if (messages is Success) {
                println("Messages: $messages")
                localDataSource.deleteChatMessages(chatRoomId)
                for (m in messages.data) localDataSource.saveChatMessage(m)
            }
        }
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        return localDataSource.observeChatMessage(id)
    }

    override suspend fun getChatMessage(id: String, forceUpdate: Boolean): Result<ChatMessage> {
        if (forceUpdate) refreshChatMessage(id)
        return localDataSource.getChatMessage(id)
    }

    override suspend fun refreshChatMessage(id: String) {
        withContext(ioDispatcher) {
            val message = remoteDataSource.getChatMessage(id)
            if (message is Success) {
                localDataSource.saveChatMessage(message.data)
            }
        }
    }

    override suspend fun isRegistered(phoneNumber: String, forceUpdate: Boolean): Result<Boolean> {
        if (forceUpdate) refreshUserDetail(phoneNumber)
        return localDataSource.isRegistered(phoneNumber)
    }

    override suspend fun saveUser(user: User): Result<Boolean> = withContext(ioDispatcher) {
        localDataSource.saveUser(user)
    }

    override suspend fun saveUserDetail(detail: UserDetail): Result<Boolean> =
        withContext(ioDispatcher) {
            val result = remoteDataSource.saveUserDetail(detail)
            if (result is Success && result.data) {
                localDataSource.saveUserDetail(detail)
            } else {
                result
            }
        }

    override suspend fun saveChatRoom(room: ChatRoom) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.saveChatRoom(room) }
            launch { localDataSource.saveChatRoom(room) }
        }
    }

    override suspend fun saveChatMessage(message: ChatMessage) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.saveChatMessage(message) }
            launch { localDataSource.saveChatMessage(message) }
        }
    }

    override suspend fun deleteAllUsers() {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteAllUsers() }
            launch { localDataSource.deleteAllUsers() }
        }
    }

    override suspend fun deleteUser(phoneNumber: String) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteUser(phoneNumber) }
            launch { localDataSource.deleteUser(phoneNumber) }
        }
    }

    override suspend fun deleteAllChatRooms() {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteAllChatRooms() }
            launch { localDataSource.deleteAllChatRooms() }
        }
    }

    override suspend fun deleteChatRoom(room: ChatRoom) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteChatRoom(room) }
            launch { localDataSource.deleteChatRoom(room) }
        }
    }

    override suspend fun deleteChatRoom(id: String) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteChatRoom(id) }
            launch { localDataSource.deleteChatRoom(id) }
        }
    }

    override suspend fun deleteAllChatMessages() {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteAllChatMessages() }
            launch { localDataSource.deleteAllChatMessages() }
        }
    }

    override suspend fun deleteChatMessages(chatRoomId: String) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteChatMessages(chatRoomId) }
            launch { localDataSource.deleteChatMessages(chatRoomId) }
        }
    }

    override suspend fun deleteChatMessage(message: ChatMessage) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteChatMessage(message) }
            launch { localDataSource.deleteChatMessage(message) }
        }
    }

    override suspend fun deleteChatMessage(id: String) {
        withContext(ioDispatcher) {
            launch { remoteDataSource.deleteChatMessage(id) }
            launch { localDataSource.deleteChatMessage(id) }
        }
    }
}