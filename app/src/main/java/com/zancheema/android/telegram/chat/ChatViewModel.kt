package com.zancheema.android.telegram.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.ChatMessage
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
    val chat = MutableStateFlow<Chat?>(null)

    val messageText = MutableLiveData<String>()

    private val _invalidChatEvent = MutableStateFlow(Event(0))
    val invalidChatEvent: Flow<Event<Int>>
        get() = _invalidChatEvent

    @FlowPreview
    val chatMessages: Flow<List<ChatMessage>>
        get() = chat
            .flatMapConcat { chat ->
                try {
                    checkNotNull(chat)
                    repository.observeChatMessages(chat.chatRoomId)
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

    fun sendMessage() {
        val message = messageText.value
        val chatValue = chat.value

        messageText.value = "" // clear the text

        if (!message.isNullOrBlank() && chatValue != null) {
            viewModelScope.launch {
                repository.saveChatMessage(
                    ChatMessage(
                        randomUUID().toString(),
                        chatValue.chatRoomId,
                        message
                    )
                )
            }
        }
    }
}