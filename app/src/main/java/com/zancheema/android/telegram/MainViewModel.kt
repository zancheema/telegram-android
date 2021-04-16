package com.zancheema.android.telegram

import androidx.lifecycle.ViewModel
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AppRepository,
    private val contentProvider: AppContentProvider
) : ViewModel() {


    fun getCurrentUserPhoneNumber(): String {
        return contentProvider.getCurrentUserPhoneNumber() ?: ""
    }

    val authStateEvent: Flow<Event<AuthState>> = flow {
        val state = if (contentProvider.isLoggedIn()) {
            val phoneNumber = getCurrentUserPhoneNumber()
            val result = repository.isRegistered(phoneNumber, true)

            if (result is Result.Success && result.data) AuthState.REGISTERED
            else AuthState.LOGGED_IN
        } else {
            AuthState.LOGGED_OUT
        }
        emit(Event(state))
    }

    enum class AuthState {
        LOGGED_IN,
        REGISTERED,
        LOGGED_OUT
    }
}