package com.zancheema.android.telegram.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
class DefaultRepositoryTest {

    // Dependencies
    private lateinit var localDataSource: AppDataSource
    private lateinit var remoteDataSource: AppDataSource

    // Class Under Test
    private lateinit var repository: DefaultRepository

    private val localUser1 = User("+178555467")
    private val localUser2 = User("+278555467")
    private val localUsers = listOf(localUser1, localUser2)

    private val remoteUser1 = User("+378555467")
    private val remoteUser2 = User("+278555467")
    private val remoteUser3 = User("+478555467")
    private val remoteUsers = listOf(remoteUser1, remoteUser2, remoteUser3)

    private val localUserDetail1 = UserDetail(localUser1.phoneNumber, "John", "Doe")
    private val localUserDetail2 = UserDetail(localUser1.phoneNumber, "Jane", "Doe")
    private val localUserDetails = listOf(localUserDetail1, localUserDetail2)

    private val remoteUserDetail1 = UserDetail(remoteUser1.phoneNumber, "Mike", "Doe")
    private val remoteUserDetail2 = UserDetail(remoteUser1.phoneNumber, "Mike", "Joe")
    private val remoteUserDetail3 = UserDetail(remoteUser1.phoneNumber, "Julian", "Joe")
    private val remoteUserDetails = listOf(remoteUserDetail1, remoteUserDetail2, remoteUserDetail3)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun initRepository() = runBlockingTest {
        localDataSource = FakeDataSource()
        remoteDataSource = FakeDataSource()

        localDataSource.apply {
            localUsers.forEach { saveUser(it) }
            localUserDetails.forEach { saveUserDetail(it) }
        }
        remoteDataSource.apply {
            remoteUsers.forEach { saveUser(it) }
            remoteUserDetails.forEach { saveUserDetail(it) }
        }

        repository = DefaultRepository(remoteDataSource, localDataSource, Dispatchers.Main)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUser_GetsUserFromLocalDataSource() = runBlockingTest {
        val users = repository.getUsers()
        assertThat((users as Success).data, `is`(localUsers))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetails_GetsUsersDetailsFromLocalDataSource() = runBlockingTest {
        val details = repository.getUserDetails()
        assertThat((details as Success).data, `is`(localUserDetails))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getUserDetailsWithForceUpdate_GetsUserDetailsForLocalUsers() = runBlockingTest {
        val details = repository.getUserDetails(true)
        assertThat((details as Success).data, `is`(localUserDetails))
    }
}