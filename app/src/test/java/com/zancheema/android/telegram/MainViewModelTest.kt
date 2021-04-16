package com.zancheema.android.telegram

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zancheema.android.telegram.MainViewModel.AuthState
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    private lateinit var repository: FakeRepository
    private lateinit var contentProvider: FakeContentProvider

    // Class Under Test
    private lateinit var viewModel: MainViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun init() {
        repository = FakeRepository()
        contentProvider = FakeContentProvider()
        viewModel = MainViewModel(repository, contentProvider)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLoggedOut_AuthStateIsLoggedOut() = runBlockingTest {
        contentProvider.loggedIn = false

        val state = viewModel.authStateEvent.first()
        MatcherAssert.assertThat(state.getContentIfNotHandled(), `is`(AuthState.LOGGED_OUT))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLoggedInButNotRegistered_AuthStateIsLoggedIn() = runBlockingTest {
        contentProvider.loggedIn = true

        val state = viewModel.authStateEvent.first()
        MatcherAssert.assertThat(state.getContentIfNotHandled(), `is`(AuthState.LOGGED_IN))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLoggedInAndRegistered_AuthStateIsRegistered() = runBlockingTest {
        val phoneNumber = "+13245558976"
        contentProvider.loggedIn = true
        contentProvider.phoneNumber = phoneNumber
        repository.saveUserDetail(UserDetail(phoneNumber, "John", "Doe"))

        val state = viewModel.authStateEvent.first()
        MatcherAssert.assertThat(state.getContentIfNotHandled(), `is`(AuthState.REGISTERED))
    }
}