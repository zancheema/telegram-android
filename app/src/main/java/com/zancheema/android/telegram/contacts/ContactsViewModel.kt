package com.zancheema.android.telegram.contacts

import androidx.lifecycle.ViewModel
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.UserDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    repository: AppRepository
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
        contactNumbers.value = phoneNumbers
    }
}