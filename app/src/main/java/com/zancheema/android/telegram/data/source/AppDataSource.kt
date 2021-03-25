package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*

interface AppDataSource {
    fun observeUser(): LiveData<Result<User>>

    fun observeUserDetailByPhoneNumber(phoneNumber: String): LiveData<Result<UserDetail>>

    fun observeAllChatRooms(): LiveData<Result<List<ChatRoom>>>

    fun observeChatRoomDetailByChatRoomId(chatRoomId: String): LiveData<Result<ChatRoomDetail>>

    fun observeChatRoomMembersByChatRoomId(chatRoomId: String): LiveData<Result<List<ChatRoomMember>>>

    fun observeChatRoomMembersByPhoneNumber(phoneNumber: String): LiveData<Result<List<ChatRoomMember>>>

    suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User>

    suspend fun getUserDetailByPhoneNumber(phoneNumber: String): Result<UserDetail>

    suspend fun getAllChatRooms(): Result<List<ChatRoom>>

    suspend fun getChatRoomDetailByChatRoomId(chatRoomId: String): Result<ChatRoomDetail>

    suspend fun getChatRoomMembersByChatRoomId(chatRoomId: String): Result<List<ChatRoomMember>>

    suspend fun getChatRoomMembersByPhoneNumber(phoneNumber: String): Result<List<ChatRoomMember>>

    suspend fun getChatMessageById(id: String): Result<ChatMessage>

    suspend fun getChatMessagesByChatRoomId(chatRoomId: String): Result<List<ChatMessage>>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(userDetail: UserDetail)

    suspend fun saveChatRoom(chatRoom: ChatRoom)

    suspend fun saveChatRoomDetail(detail: ChatRoomDetail)

    suspend fun saveChatRoomMember(member: ChatRoomMember)

    suspend fun saveChatMessage(message: ChatMessage)

    suspend fun deleteUser(user: User)

    suspend fun deleteUserWithPhoneNumber(phoneNumber: String)

    suspend fun deleteChatRoom(chatRoom: ChatRoom)

    suspend fun deleteChatRoomWithId(id: String)

    suspend fun deleteAllChatRooms()

    suspend fun deleteChatMessage(message: ChatMessage)

    suspend fun deleteChatMessageWithId(id: String)

    suspend fun deleteChatMessagesByChatRoomId(chatRoomId: String)
}