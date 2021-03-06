package com.zancheema.android.telegram.data.source.local

import android.util.Log
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppDataSource
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.data.source.local.entity.asDomainModel
import com.zancheema.android.telegram.data.source.local.view.asDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val TAG = "LocalDataSource"

class LocalDataSource(
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppDataSource {
    override fun observeUsers(): Flow<Result<List<User>>> {
        return database.userDao().observeAll()
            .map { users -> Success(users.map { it.asDomainModel() }) }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return try {
            val users = database.userDao().getAll()
                .map { it.asDomainModel() }
            Success(users)
        } catch (e: Exception) {
            Log.w(TAG, "getUsers: ", e)
            Error(e)
        }
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        return database.userDetailDao().observeAll()
            .map { details -> Success(details.map { it.asDomainModel() }) }
    }

    override suspend fun getUserDetails(): Result<List<UserDetail>> {
        return try {
            val details = database.userDetailDao().getAll()
                .map { it.asDomainModel() }
            Success(details)
        } catch (e: Exception) {
            Log.w(TAG, "getUserDetails: ", e)
            Error(e)
        }
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        return database.userDetailDao().observeUserDetailsByPhoneNumbers(phoneNumbers)
            .map { details -> Success(details.map { it.asDomainModel() }) }
    }

    override suspend fun getUserDetails(
        phoneNumbers: List<String>
    ): Result<List<UserDetail>> {
        return try {
            val details = database.userDetailDao()
                .getUserDetailsByPhoneNumbers(phoneNumbers)
                .map { it.asDomainModel() }
            Success(details)
        } catch (e: Exception) {
            Log.w(TAG, "getUserDetails(phoneNumbers): ", e)
            Error(e)
        }
    }

    override fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>> {
        return database.userDetailDao().observeUserDetailByPhoneNumber(phoneNumber)
            .map { detail ->
                if (detail == null) getError("UserDetail does not exist locally")
                else Success(detail.asDomainModel())
            }
    }

    override suspend fun getUserDetail(phoneNumber: String): Result<UserDetail> {
        return try {
            val detail = database.userDetailDao()
                .getUserDetailByPhoneNumber(phoneNumber)
                ?.asDomainModel()
                ?: error("User Detail not found")
            Success(detail)
        } catch (e: Exception) {
            Log.w(TAG, "getUserDetail(phoneNumbers): ", e)
            Error(e)
        }
    }

    override fun observeChatRooms(): Flow<Result<List<ChatRoom>>> {
        return database.chatRoomDao().observeAll()
            .map { rooms -> Success(rooms.map { it.asDomainModel() }) }
    }

    override suspend fun getChatRooms(): Result<List<ChatRoom>> {
        return try {
            val rooms = database.chatRoomDao()
                .getAll()
                .map { it.asDomainModel() }
            Success(rooms)
        } catch (e: Exception) {
            Log.w(TAG, "getChatRooms: ", e)
            Error(e)
        }
    }

    override fun observeChatRoom(id: String): Flow<Result<ChatRoom>> {
        return database.chatRoomDao().observeById(id)
            .map { room ->
                if (room == null) getError("ChatRoom does not exist locally")
                else Success(room.asDomainModel())
            }
    }

    override suspend fun getChatRoom(id: String): Result<ChatRoom> {
        return try {
            val rooms = database.chatRoomDao()
                .getById(id)
                ?.asDomainModel()
                ?: error("Chat room not found")
            Success(rooms)
        } catch (e: Exception) {
            Log.w(TAG, "getChatRooms: ", e)
            Error(e)
        }
    }

    override fun observeChats(): Flow<Result<List<Chat>>> {
        return database.chatRoomDao().observeAllChats()
            .map { chats -> Success(chats.map { it.asDomainModel() }) }
    }

    override suspend fun getChats(): Result<List<Chat>> {
        return try {
            val chats = database.chatRoomDao()
                .getAllChats()
                .map { it.asDomainModel() }
            return Success(chats)
        } catch (e: Exception) {
            Log.w(TAG, "getChats: ", e)
            Error(e)
        }
    }

    override fun observeChatMessages(): Flow<Result<List<ChatMessage>>> {
        return database.chatMessageDao().observeAll()
            .map { messages -> Success(messages.map { it.asDomainModel() }) }
    }

    override suspend fun getChatMessages(): Result<List<ChatMessage>> {
        return try {
            val messages = database.chatMessageDao()
                .getAll()
                .map { it.asDomainModel() }
            Success(messages)
        } catch (e: Exception) {
            Log.w(TAG, "getChatMessages: ", e)
            Error(e)
        }
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
        return database.chatMessageDao().observeByChatRoomId(chatRoomId)
            .map { messages -> Success(messages.map { it.asDomainModel() }) }
    }

    override suspend fun getChatMessages(
        chatRoomId: String
    ): Result<List<ChatMessage>> {
        return try {
            val messages = database.chatMessageDao()
                .getByChatRoomId(chatRoomId)
                .map { it.asDomainModel() }
            Success(messages)
        } catch (e: Exception) {
            Log.w(TAG, "getChatMessages: ", e)
            Error(e)
        }
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        return database.chatMessageDao().observeById(id)
            .map { message ->
                if (message == null) getError("ChatMessage does not exist locally")
                else Success(message.asDomainModel())
            }
    }

    private fun getError(message: String): Error {
        return Error(Exception(message))
    }

    override suspend fun getChatMessage(id: String): Result<ChatMessage> {
        return try {
            val message = database.chatMessageDao()
                .getById(id)
                ?.asDomainModel()
                ?: error("chat message not found")
            Success(message)
        } catch (e: Exception) {
            Log.w(TAG, "getChatMessage: ", e)
            Error(e)
        }
    }

    override suspend fun isRegistered(phoneNumber: String): Result<Boolean> {
        return try {
            val detail = database.userDetailDao().getUserDetailByPhoneNumber(phoneNumber)
            Success(detail != null)
        } catch (e: Exception) {
            Log.w(TAG, "isRegistered: ", e)
            Error(e)
        }
    }

    override suspend fun saveUser(user: User): Result<Boolean> = withContext(ioDispatcher) {
        try {
            database.userDao().insertUser(user.asDatabaseEntity())
            Success(true)
        } catch (e: Exception) {
            Log.w(TAG, "saveUserDetail: ", e)
            Error(e)
        }
    }

    override suspend fun saveUserDetail(detail: UserDetail): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                database.userDetailDao().insertUserDetail(detail.asDatabaseEntity())
                Success(true)
            } catch (e: Exception) {
                Log.w(TAG, "saveUserDetail: ", e)
                Error(e)
            }
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
