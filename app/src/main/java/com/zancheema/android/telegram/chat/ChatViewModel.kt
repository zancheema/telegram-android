package com.zancheema.android.telegram.chat

import androidx.lifecycle.ViewModel
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ViewModelScoped
class ChatViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val chat = MutableStateFlow<Chat?>(null)

    private val _invalidChatEvent = MutableStateFlow(Event(0))
    val invalidChatEvent: Flow<Event<Int>>
        get() = _invalidChatEvent

    @FlowPreview
    val messages: Flow<List<ChatMessage>>
        get() = chat
            .flatMapConcat { chat ->
                try {
                    checkNotNull(chat)
                    repository.observeChatMessagesByChatRoomId(chat.chatRoomId)
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
}