package com.zancheema.android.telegram.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.LocalDataSource
import com.zancheema.android.telegram.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {

    private lateinit var database: AppDatabase

    // class under test
    private lateinit var localDataSource: LocalDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        localDataSource = LocalDataSource(database, Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveUserAndGetById() = runBlockingTest {
        val user = User("+17685559054")
        localDataSource.saveUser(user)

        val loaded = localDataSource.getUserByPhoneNumber(user.phoneNumber)
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(user))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun saveAndGetFetchUserDetailByPhoneNumber() = runBlockingTest {
        val user = User("+17685559054")
        localDataSource.saveUser(user)
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUserDetail(userDetail)

        val loaded = localDataSource.getUserDetailByPhoneNumber(userDetail.phoneNumber)
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(userDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserAndFetchByPhoneNumber_ReturnsError() = runBlockingTest {
        val user = User("+17685559054")
        localDataSource.saveUser(user)

        localDataSource.deleteUser(user)

        val loaded = localDataSource.getUserByPhoneNumber(user.phoneNumber)
        assertThat(loaded is Error, `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertUserDetailAgainForSameUserUpdatesPreviousDetail() = runBlockingTest {
        val user = User("+17685559054")
        localDataSource.saveUser(user)
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUserDetail(userDetail)
        val updatedUserDetail = UserDetail(user.phoneNumber, "Jane", "Doe")
        localDataSource.saveUserDetail(updatedUserDetail)

        val loaded = localDataSource.getUserDetailByPhoneNumber(user.phoneNumber)
        assertThat(loaded.succeeded, `is`(true))
        loaded as Success
        assertThat(loaded.data, `is`(not(userDetail)))
        assertThat(loaded.data, `is`(updatedUserDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserAndFetchUserDetail_ReturnsError() = runBlockingTest {
        val user = User("+17685559054")
        localDataSource.saveUser(user)
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        localDataSource.saveUserDetail(userDetail)

        localDataSource.deleteUser(user)

        val loaded = localDataSource.getUserDetailByPhoneNumber(userDetail.phoneNumber)
        assertThat(loaded is Error, `is`(true))
    }
}