package com.zancheema.android.telegram.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.UserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val repository: AppRepository,
    private val contentProvider: AppContentProvider
) : ViewModel() {
    private val contactNumbers = MutableStateFlow<List<String>>(emptyList())

    @FlowPreview
    val userDetails: Flow<List<UserDetail>> =
        contactNumbers
            .flatMapConcat { phoneNumbers ->
                repository.observeUserDetails(phoneNumbers)
            }
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    else -> emptyList()
                }
            }

    fun setContactNumbers(phoneNumbers: List<String>) {
        viewModelScope.launch {
            repository.refreshUsers(phoneNumbers)
            repository.refreshUserDetails(phoneNumbers)

            for (number in phoneNumbers) {
                val roomId = getChatRoomId(number)
                repository.saveChatRoom(ChatRoom(roomId, number))
                repository.refreshChatRooms()
            }
        }
        contactNumbers.value = phoneNumbers
    }

    private fun getChatRoomId(phoneNumber: String): String {
        val list = listOf(contentProvider.getCurrentUserPhoneNumber()!!, phoneNumber)
            .sortedBy { it }
        return list[0] + list[1]
    }
}