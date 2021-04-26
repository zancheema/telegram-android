package com.zancheema.android.telegram.data.source.remote

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.AppDataSource
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.data.source.remote.dto.ChatMessageDTO
import com.zancheema.android.telegram.data.source.remote.dto.ChatRoomDTO
import com.zancheema.android.telegram.data.source.remote.dto.UserDTO
import com.zancheema.android.telegram.data.source.remote.dto.asDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "RemoteDataSource"

class RemoteDataSource(
    private val contentProvider: AppContentProvider,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppDataSource {

    private fun usersCollection() = Firebase.firestore.collection("users")

    private fun currentUserDoc() = usersCollection()
        .document(contentProvider.getCurrentUserPhoneNumber() ?: "")

    private fun chatRoomsCollection() = currentUserDoc()
        .collection("chatRooms")

    private fun chatMessagesCollection(chatRoomId: String) = chatRoomsCollection()
        .document(chatRoomId)
        .collection("chatMessages")

    override fun observeUsers(): Flow<Result<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(phoneNumbers: List<String>): Result<List<UserDetail>> {
        return try {
            val users = usersCollection().whereIn("phoneNumber", phoneNumbers)
                .get()
                .await()
                .toObjects(UserDTO::class.java)
            Success(users.map { it.asDomainModel() })
        } catch (e: Exception) {
            Log.w(TAG, "getUserDetails: ", e)
            Error(e)
        }
    }

    override fun observeUserDetail(phoneNumber: String): Flow<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetail(phoneNumber: String): Result<UserDetail> {
        return try {
            val user = usersCollection()
                .document(phoneNumber)
                .get()
                .await()
                .toObject(UserDTO::class.java)
                ?: error("UserDetail not found")
            Success(user.asDomainModel())
        } catch (e: Exception) {
            Log.w(TAG, "getUserDetail: ")
            Error(e)
        }
    }

    override fun observeChatRooms(): Flow<Result<List<ChatRoom>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRooms(): Result<List<ChatRoom>> {
        return try {
            val rooms = chatRoomsCollection()
                .get()
                .await()
                .toObjects(ChatRoomDTO::class.java)
                .map { it.asDomainModel() }
            Success(rooms)
        } catch (e: Exception) {
            Log.w(TAG, "getChatRooms: ")
            Error(e)
        }
    }

    override fun observeChatRoom(id: String): Flow<Result<ChatRoom>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatRoom(id: String): Result<ChatRoom> {
        return try {
            val room = chatRoomsCollection()
                .document(id)
                .get()
                .await()
                .toObject(ChatRoomDTO::class.java)
                ?: error("ChatRoom not found")
            Success(room.asDomainModel())
        } catch (e: Exception) {
            Log.w(TAG, "getChatRoom: ")
            Error(e)
        }
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

    override suspend fun getChatMessages(): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessages(chatRoomId: String): Result<List<ChatMessage>> {
        return try {
            val messages = chatMessagesCollection(chatRoomId)
                .get()
                .await()
                .toObjects(ChatMessageDTO::class.java)
                .map { it.asDomainModel() }

            Success(messages)
        } catch (e: Exception) {
            Log.w(TAG, "getChatMessages: ")
            Error(e)
        }
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun getChatMessage(id: String): Result<ChatMessage> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegistered(phoneNumber: String): Result<Boolean> {
        return try {
            Success(getUserDetail(phoneNumber) is Success)
        } catch (e: Exception) {
            Log.w(TAG, "isRegistered: ")
            Error(e)
        }
    }

    override suspend fun saveUser(user: User): Result<Boolean> {
        // TODO: 26/04/2021 implementation not required yet
        return Success(true)
    }

    override suspend fun saveUserDetail(detail: UserDetail): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                usersCollection()
                    .document(detail.phoneNumber)
                    .set(detail.asDataTransferObject())
                    .await()
                Success(true)
            } catch (e: Exception) {
                Log.w(TAG, "saveUserDetail: ")
                Error(e)
            }
        }

    override suspend fun saveChatMessage(message: ChatMessage) {
        try {
            withContext(ioDispatcher) {
                chatMessagesCollection(message.chatRoomId)
                    .document(message.id)
                    .set(message.asDataTransferObject())
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "saveChatMessage: ", e)
        }
    }

    override suspend fun saveChatRoom(room: ChatRoom) {
        try {
            withContext(ioDispatcher) {
                chatRoomsCollection()
                    .document(room.id)
                    .set(room.asDataTransferObject())
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "saveChatRoom: ", e)
        }
    }

    override suspend fun deleteAllUsers() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: User) {
        try {
            withContext(ioDispatcher) {
                usersCollection().document(user.phoneNumber)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteUser: ", e)
        }
    }

    override suspend fun deleteUser(phoneNumber: String) {
        try {
            withContext(ioDispatcher) {
                usersCollection()
                    .document(phoneNumber)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteUser: ", e)
        }
    }

    override suspend fun deleteAllChatRooms() {
        try {
            withContext(ioDispatcher) {
                chatRoomsCollection()
                    .get()
                    .await()
                    .toObjects(ChatRoomDTO::class.java)
                    .run {
                        for (room in this) deleteChatRoom(room.asDomainModel())
                    }
                Log.w(TAG, "deleteAllChatRooms Successful")
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteAllChatRooms: ", e)
        }
    }

    override suspend fun deleteChatRoom(room: ChatRoom) {
        try {
            withContext(ioDispatcher) {
                chatRoomsCollection().document(room.id)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteChatRoom: ", e)
        }
    }

    override suspend fun deleteChatRoom(id: String) {
        try {
            withContext(ioDispatcher) {
                chatRoomsCollection()
                    .document(id)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteChatRoom: ", e)
        }
    }

    override suspend fun deleteAllChatMessages() {
        TODO("not yet implemented")
    }

    override suspend fun deleteChatMessages(chatRoomId: String) {
        try {
            withContext(ioDispatcher) {
                chatMessagesCollection(chatRoomId)
                    .get()
                    .await()
                    .toObjects(ChatMessageDTO::class.java)
                    .run {
                        for (m in this) deleteChatMessage(m.asDomainModel())
                    }
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteChatMessages: ", e)
        }
    }

    override suspend fun deleteChatMessage(message: ChatMessage) {
        try {
            withContext(ioDispatcher) {
                chatMessagesCollection(message.chatRoomId)
                    .document(message.id)
                    .delete()
                    .await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "deleteChatMessages: ", e)
        }
    }

    override suspend fun deleteChatMessage(id: String) {
        TODO("Not yet implemented")
    }
}