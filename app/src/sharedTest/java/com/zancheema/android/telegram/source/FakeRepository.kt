package com.zancheema.android.telegram.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepository @Inject constructor() : AppRepository {

    private val observableUser = MutableLiveData<User>(null)
    private val observableUsers = MutableLiveData<List<User>>(emptyList())
    private var observableUserDetail = MutableLiveData<UserDetail>(null)
    private val observableChatMessages = MutableLiveData<List<ChatMessage>>()

    override suspend fun getUserDetail(): Result<UserDetail> {
        return observableUserDetail.value?.let {
            Success(it)
        } ?: Error(Exception("user does not exist"))
    }

    override fun observeMessages(phoneNumber: String): LiveData<Result<List<ChatMessage>>> =
        observableChatMessages.map { messages ->
            val msg = messages.filter {
                it.from.phoneNumber == phoneNumber || it.to.phoneNumber == phoneNumber
            }
            Success(msg)
        }

    override suspend fun getMessages(phoneNumber: String): Result<List<ChatMessage>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetail(): LiveData<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            for (u in observableUsers.value!!) {
                if (u.phoneNumber == phoneNumber) return@withContext Success(true)
            }
            Success(false)
        }

    override suspend fun getChats(): Result<List<Chat>> {
        TODO("Not yet implemented")
    }

    override fun observeChats(): LiveData<Result<List<Chat>>> =
        observableChatMessages.map { messages ->
            Success(getChats(messages, observableUserDetail.value!!))
        }

    private fun getChats(messages: List<ChatMessage>, me: UserDetail): List<Chat> {
        val chatHeads = mutableMapOf<String, Chat>()
        for (msg in messages) {
            val isMine = msg.from == me
            val other = if (isMine) msg.to else msg.from
            val chatHead = chatHeads[other.phoneNumber]
            if (chatHead == null || msg.timestamp >= chatHead.timestamp) {
                chatHeads[other.phoneNumber] = getChat(msg, isMine)
            }
        }

        return chatHeads.values.toList()
    }

    private fun getChat(
        msg: ChatMessage,
        isMine: Boolean
    ): Chat {
        val other = if (isMine) msg.to else msg.from
        return Chat(
            other.fullName,
            other.phoneNumber,
            msg.message,
            msg.timestamp
        )
    }

    override fun observeUserExists(): LiveData<Result<Boolean>> =
        observableUser.map { Success(it != null) }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.Main) {
        // save user
        observableUser.value = user
        observableUsers.value = observableUsers.value!!.toMutableList().apply { add(user) }
    }

    override suspend fun saveUserDetail(detail: UserDetail) = withContext(Dispatchers.Main) {
        observableUserDetail.value = detail
    }

    override suspend fun saveMessage(message: ChatMessage) {
        val tmp = observableChatMessages.value?.toMutableList() ?: mutableListOf()
        tmp.add(message)
        withContext(Dispatchers.Main) {
            observableChatMessages.value = tmp
        }
    }
}