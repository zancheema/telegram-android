package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.flow.Flow

class DefaultRepository : AppRepository {
    override suspend fun getUserDetails(forceUpdate: Boolean): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUserDetails() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUserDetail(phoneNumber: String) {
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

    override suspend fun refreshChatMessage(id: String) {
        TODO("Not yet implemented")
    }

    override fun observeUsers(): Flow<Result<List<User>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUsers() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshUser(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserDetail(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(phoneNumbers: List<String>): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(
        phoneNumbers: List<String>,
        forceUpdate: Boolean
    ): Result<List<UserDetail>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetails(): Flow<Result<List<UserDetail>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetail(phoneNumber: String, forceUpdate: Boolean): Result<UserDetail> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun observeChatMessages(chatRoomId: String): Flow<Result<List<ChatMessage>>> {
        TODO("Not yet implemented")
    }

    override fun observeChatMessage(id: String): Flow<Result<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegistered(phoneNumber: String, forceUpdate: Boolean): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun saveUserDetail(detail: UserDetail) {
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

    override suspend fun deleteAllUserDetails() {
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

    override suspend fun saveUser(user: User) {
        TODO("Not yet implemented")
    }
}