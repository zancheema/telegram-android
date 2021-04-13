package com.zancheema.android.telegram.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.succeeded
import com.zancheema.android.telegram.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    private lateinit var repository: FakeRepository
    private lateinit var viewModel: RegisterViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun initializeViewModel() {
        repository = FakeRepository()
        viewModel = RegisterViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserDetail_phoneNumberNotInitialized_GeneratesInvalidPhoneNumberEvent() =
        runBlockingTest {
            viewModel.saveUserDetail()

            val event = viewModel.messageText.first()
            assertThat(event?.getContentIfNotHandled(), `is`(R.string.invalid_phone_number))
        }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserDetailWithInvalidPhoneNumber_GeneratesInvalidPhoneNumberEvent() = runBlockingTest {
        val phoneNumber = "+13425557960"

        viewModel.phoneNumber = phoneNumber
        viewModel.firstName.value = "John"
        viewModel.saveUserDetail()

        val event = viewModel.messageText.first()
        assertThat(event?.getContentIfNotHandled(), `is`(R.string.invalid_phone_number))
        assertThat(
            repository.getUserDetail(phoneNumber),
            instanceOf(Error::class.java)
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserDetail_FirstNameIsEmpty_GeneratesEmptyNameEvent() = runBlockingTest {
        val phoneNumber = "+13425557960"
        repository.saveUser(User(phoneNumber)) // to pass foreign key constraint

        viewModel.phoneNumber = phoneNumber
        viewModel.saveUserDetail()

        val event = viewModel.messageText.first()
        assertThat(event?.getContentIfNotHandled(), `is`(R.string.empty_first_name))
        assertThat(
            repository.getUserDetail(phoneNumber),
            instanceOf(Error::class.java)
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun lastNameIsOptionalInSavingUserDetail() = runBlockingTest {
        val phoneNumber = "+13425557960"
        val firstName = "John"
        repository.saveUser(User(phoneNumber)) // to pass foreign key constraint

        viewModel.phoneNumber = phoneNumber
        viewModel.firstName.value = firstName
        viewModel.saveUserDetail()

        // user detail is still saved, if the lastName is not provided
        val userDetail = repository.getUserDetail(phoneNumber)
        assertThat(userDetail.succeeded, `is`(true))
        userDetail as Success
        assertThat(userDetail.data.phoneNumber, `is`(phoneNumber))
        assertThat(userDetail.data.firstName, `is`(firstName))
        assertThat(userDetail.data.lastName, `is`(""))

        val event = viewModel.userRegisteredEvent.first()
        assertThat(event?.getContentIfNotHandled(), `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserWithValidDetail_SavesUserDetailInRepository() = runBlockingTest {
        val phoneNumber = "+13425557960"
        val firstName = "John"
        val lastName = "Doe"
        repository.saveUser(User(phoneNumber)) // to pass foreign key constraint

        viewModel.phoneNumber = phoneNumber
        viewModel.firstName.value = firstName
        viewModel.lastName.value = lastName
        viewModel.saveUserDetail()

        val userDetail = repository.getUserDetail(phoneNumber)
        assertThat(userDetail.succeeded, `is`(true))
        userDetail as Success
        assertThat(userDetail.data.phoneNumber, `is`(phoneNumber))
        assertThat(userDetail.data.firstName, `is`(firstName))
        assertThat(userDetail.data.lastName, `is`(lastName))

        val event = viewModel.userRegisteredEvent.first()
        assertThat(event?.getContentIfNotHandled(), `is`(true))
    }
}