package com.zancheema.android.telegram.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    val countryCode = MutableLiveData(1)
    val countryName = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()

    private val _signInEvent = MutableLiveData<Event<String>>()
    val signInEvent: LiveData<Event<String>>
        get() = _signInEvent

    private val _invalidCredentialsEvent = MutableLiveData<Event<Int>>()
    val invalidCredentialsEvent: LiveData<Event<Int>>
        get() = _invalidCredentialsEvent

    fun signIn() {
        val number = phoneNumber.value
        if (number == null || number.length < 10) {
            _invalidCredentialsEvent.value = Event(R.string.invalid_phone_number)
        }
        _signInEvent.value = Event("+${countryCode.value}${phoneNumber.value}")
    }
}