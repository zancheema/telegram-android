package com.zancheema.android.telegram.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.UserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID.randomUUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    private val chatRoom = MutableStateFlow<ChatRoom?>(null)

    private val _userDetail = MutableStateFlow<UserDetail?>(null)
    val userDetail: Flow<UserDetail?>
        get() = _userDetail

    val messageText = MutableLiveData<String>()

    private val _invalidChatEvent = MutableStateFlow(Event(0))
    val invalidChatEvent: Flow<Event<Int>>
        get() = _invalidChatEvent

    @FlowPreview
    val chatMessages: Flow<List<ChatMessage>>
        get() = chatRoom
            .flatMapConcat { room ->
                try {
                    checkNotNull(room)
                    repository.observeChatMessages(room.id)
                } catch (e: Exception) {
                    _invalidChatEvent.value = Event(R.string.invalid_chat)
                    flowOf(Error(e))
                }
            }
            .map { result ->
                when (result) {
                    is Success -> result.data
                    else -> emptyList()
                }
            }

    fun setChatRoomId(id: String) {
        viewModelScope.launch {
            when (val room = repository.getChatRoom(id, true)) {
                is Success -> {
                    chatRoom.value = room.data
                    when (val detail = repository.getUserDetail(room.data.phoneNumber, true)) {
                        is Success -> _userDetail.value = detail.data
                        else -> TODO("not yet implemented")
                    }
                }
                else -> TODO("not yet implemented")
            }
        }
    }

    fun sendMessage() {
        val message = messageText.value
        val chatValue = chatRoom.value

        messageText.value = "" // clear the text

        if (!message.isNullOrBlank() && chatValue != null) {
            viewModelScope.launch {
                repository.saveChatMessage(
                    ChatMessage(
                        randomUUID().toString(),
                        chatValue.id,
                        message
                    )
                )
            }
        }
    }
}