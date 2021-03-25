package com.zancheema.android.telegram.chat

import androidx.lifecycle.*
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.data.Result.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val otherUser = MutableLiveData<UserDetail>()

    val newMessage = MutableLiveData<String>()

    val messages: LiveData<List<ChatMessage>> = otherUser.switchMap { user ->
        repository.observeMessages(user.phoneNumber).map {
            (it as Success).data
        }
    }

    fun setOtherUserDetail(user: UserDetail) {
        otherUser.value = user
    }

    fun sendMessage() {
        val msg = newMessage.value
        newMessage.value = ""   // clear the message
        if (msg == null || msg.isBlank()) return

        viewModelScope.launch {
            val from = (repository.getUserDetail() as Success).data
            val to = otherUser.value ?: return@launch
            sendMessage(msg, from, to)
        }
    }

    private suspend fun sendMessage(
        msg: String,
        from: UserDetail,
        to: UserDetail
    ) {
        repository.saveMessage(ChatMessage(msg, from, to, isMine = true))
    }
}