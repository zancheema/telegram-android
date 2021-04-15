package com.zancheema.android.telegram.chats

import androidx.lifecycle.ViewModel
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.Chat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatsViewModel(
    repository: AppRepository
) : ViewModel() {

    val chats: Flow<List<Chat>> =
        repository.observeChats().map { result ->
            when (result) {
                is Success -> result.data
                else -> emptyList()
            }
        }

    val emptyChatsEvent: Flow<Event<Boolean>> =
        chats.map { Event(it.isEmpty()) }
}