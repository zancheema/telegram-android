package com.zancheema.android.telegram.contacts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.data.succeeded
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContactsViewModelTest {

    private lateinit var repository: FakeRepository
    private lateinit var contentProvider: FakeContentProvider

    private lateinit var viewModel: ContactsViewModel

    private val user1 = User("+12345556543")
    private val user2 = User("+312345556703")
    private val user3 = User("+12095556123")
    private val userDetail1 = UserDetail(user1.phoneNumber, "John", "Doe")
    private val userDetail2 = UserDetail(user2.phoneNumber, "John", "Doe")
    private val userDetail3 = UserDetail(user3.phoneNumber, "John", "Doe")

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun initViewModel() = runBlockingTest {
        repository = FakeRepository()
        repository.apply {
            saveUser(user1)
            saveUser(user2)
            saveUser(user3)
            saveUserDetail(userDetail1)
            saveUserDetail(userDetail2)
            saveUserDetail(userDetail3)
        }
        contentProvider = FakeContentProvider()
        contentProvider.currentPhoneNumber = user1.phoneNumber

        viewModel = ContactsViewModel(repository, contentProvider)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun userDetailsReturnEmptyListIfContactsNumbersNotSet() = runBlockingTest {
        val details = viewModel.userDetails.first()

        assertThat(details, `is`(emptyList()))
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun userDetailsReturnsUserDetailsByPhoneNumbers() = runBlockingTest {
        viewModel.setContactNumbers(listOf(user1.phoneNumber, user2.phoneNumber))
        val loaded = viewModel.userDetails.first()

        assertThat(loaded.size, `is`(2))
        assertThat(loaded.contains(userDetail1), `is`(true))
        assertThat(loaded.contains(userDetail2), `is`(true))
        assertThat(loaded.contains(userDetail3), `is`(false))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun setContactNumbersCreatesChatRoomsWithTheNumbers() = runBlockingTest {
        viewModel.setContactNumbers(listOf(user2.phoneNumber, user3.phoneNumber))

        val loaded = repository.getChatRooms()

        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data.size, `is`(2))
        assertThat(loaded.data[0].phoneNumber, `is`(user2.phoneNumber))
        assertThat(loaded.data[1].phoneNumber, `is`(user3.phoneNumber))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun openChatGeneratesOpenChatEvent() = runBlockingTest {
        viewModel.openChat(userDetail2)

        val event = viewModel.openChatEvent.first()
        assertThat(event?.getContentIfNotHandled(), `is`(notNullValue()))
    }
}