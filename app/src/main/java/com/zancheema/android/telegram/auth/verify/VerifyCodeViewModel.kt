package com.zancheema.android.telegram.auth.verify

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.Event
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.util.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "VerifyCodeViewModel"

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    val phoneNumber = MutableLiveData<String>()
    val verificationId = MutableLiveData<String>()
    val smsCode = MutableLiveData<String>()

    private val _showChatsEvent = MutableLiveData<Event<Boolean>>()
    val showChatsEvent: LiveData<Event<Boolean>>
        get() = _showChatsEvent

    private val _showRegistrationEvent = MutableLiveData<Event<String>>()
    val showRegistrationEvent: LiveData<Event<String>>
        get() = _showRegistrationEvent

    fun verify() {
        viewModelScope.launch {
            repository.isRegistered(phoneNumber.value!!).let { isRegistered ->
                if (isRegistered is Success) {
                    signIn(isRegistered.data)
                } else if (isRegistered is Error) {
                    Log.d(TAG, "verify: error: ${isRegistered.exception}")
                }
            }
        }
    }

    private fun signIn(isRegistered: Boolean) {
        val credential = PhoneAuthProvider.getCredential(verificationId.value!!, smsCode.value!!)
        viewModelScope.launch {
            wrapEspressoIdlingResource {
                try {
                    val result = Firebase.auth.signInWithCredential(credential).await()
                    val user = User(result.user!!.phoneNumber!!)
                    repository.saveUser(user)
                    if (isRegistered) showChats()
                    else showRegistration(user.phoneNumber)
                } catch (e: Exception) {
                    Log.d(TAG, "signIn: error: $e")
                }
            }
        }
    }

    private fun showChats() {
        _showChatsEvent.value = Event(true)
    }

    private fun showRegistration(phoneNumber: String) {
        _showRegistrationEvent.value = Event(phoneNumber)
    }
}