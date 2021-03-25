package com.zancheema.android.telegram.auth.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    val phoneNumber = MutableLiveData<String>()
    val verificationId = MutableLiveData<String>()
    val smsCode = MutableLiveData<String>()

    private val _verificationEvent = MutableLiveData<Event<AuthCredential>>()
    val verificationEvent: LiveData<Event<AuthCredential>>
        get() = _verificationEvent

    private val _showChatsEvent = MutableLiveData<Event<Boolean>>()
    val showChatsEvent: LiveData<Event<Boolean>>
        get() = _showChatsEvent

    private val _showRegistrationEvent = MutableLiveData<Event<Boolean>>()
    val showRegistrationEvent: LiveData<Event<Boolean>>
        get() = _showRegistrationEvent

    fun verify() {
        viewModelScope.launch {
            val isRegistered = repository.isRegisteredPhoneNumber(phoneNumber.value!!)
            if (isRegistered is Success && isRegistered.data) {
                showChats()
            } else {
                _verificationEvent.value =
                    Event(PhoneAuthProvider.getCredential(verificationId.value!!, smsCode.value!!))
            }
        }
    }

    fun showChats() {
        _showChatsEvent.value = Event(true)
    }

    fun showRegistration() {
        _showRegistrationEvent.value = Event(true)
    }
}