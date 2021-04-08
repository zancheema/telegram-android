package com.zancheema.android.telegram.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.UserDetail
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class RegisterViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    lateinit var phoneNumber: String

    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()

    private val _messageText = MutableStateFlow<Event<Int>?>(null)
    val messageText: Flow<Event<Int>?>
        get() = _messageText

    private val _userRegisteredEvent = MutableStateFlow<Event<Boolean>?>(null)
    val userRegisteredEvent: Flow<Event<Boolean>?>
        get() = _userRegisteredEvent

    fun saveUserDetail() {
        val fName = firstName.value

        if (!::phoneNumber.isInitialized) {
            generateInvalidPhoneNumberEvent()
        } else if (fName.isNullOrBlank()) {
            generateEmptyFirstNameEvent()
        } else {
            val lName = lastName.value ?: ""
            // TODO: upload photo onto the server
            //  and set the download url as [photoUrl]
            val photoUrl = ""
            val userDetail = UserDetail(phoneNumber, fName, lName, photoUrl)
            viewModelScope.launch {
                try {
                    repository.saveUserDetail(userDetail)
                    generateUserRegisteredEvent()
                } catch (e: Exception) {
                    generateInvalidPhoneNumberEvent()
                }
            }
        }
    }

    private fun generateUserRegisteredEvent() {
        _userRegisteredEvent.value = Event(true)
    }

    private fun generateEmptyFirstNameEvent() {
        _messageText.value = Event(R.string.empty_first_name)
    }

    private fun generateInvalidPhoneNumberEvent() {
        _messageText.value = Event(R.string.invalid_phone_number)
    }
}