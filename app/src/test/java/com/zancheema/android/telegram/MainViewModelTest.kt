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
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
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

    /**
     * Only dependencies for the [viewModel] are initialized
     * because the initializing time of [viewModel] is inconsistent in different tests
     */
    @Before
    fun initializeDependencies() {
        repository = FakeRepository()
        contentProvider = FakeContentProvider()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLoggedOut_AuthStateIsLoggedOut() = runBlockingTest {
        contentProvider.loggedIn = false
        viewModel = MainViewModel(repository, contentProvider)

        val state = viewModel.authStateEvent.first()
        assertThat(state.getContentIfNotHandled(), `is`(AuthState.LOGGED_OUT))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLoggedInButNotRegistered_AuthStateIsLoggedIn() = runBlockingTest {
        contentProvider.loggedIn = true
        viewModel = MainViewModel(repository, contentProvider)

        val state = viewModel.authStateEvent.first()
        assertThat(state.getContentIfNotHandled(), `is`(AuthState.LOGGED_IN))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun userLoggedInAndRegistered_AuthStateIsRegistered() = runBlockingTest {
        val phoneNumber = "+13245558976"
        contentProvider.loggedIn = true
        contentProvider.phoneNumber = phoneNumber
        repository.saveUserDetail(UserDetail(phoneNumber, "John", "Doe"))
        viewModel = MainViewModel(repository, contentProvider)

        val state = viewModel.authStateEvent.first()
        assertThat(state.getContentIfNotHandled(), `is`(AuthState.REGISTERED))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun currentUserIsNullIfUserIsLoggedOut() = runBlockingTest {
        contentProvider.loggedIn = false
        viewModel = MainViewModel(repository, contentProvider)

        assertThat(viewModel.currentUser.first(), `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun currentUserIsNullIfUserIsLoggedInButNotRegistered() = runBlockingTest {
        contentProvider.loggedIn = true
        viewModel = MainViewModel(repository, contentProvider)

        assertThat(viewModel.currentUser.first(), `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun currentUserIsNotNullIfUserIsLoggedInAndRegistered() = runBlockingTest {
        contentProvider.loggedIn = true // login
        val phoneNumber = "+13245558976"
        contentProvider.phoneNumber = phoneNumber
        val userDetail = UserDetail(phoneNumber, "John", "Doe")
        repository.saveUserDetail(userDetail) // register
        viewModel = MainViewModel(repository, contentProvider)

        assertThat(viewModel.currentUser.first(), `is`(userDetail))
    }
}