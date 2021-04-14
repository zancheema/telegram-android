package com.zancheema.android.telegram.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.entity.DbUser
import com.zancheema.android.telegram.data.source.local.entity.DbUserDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDetailDaoTest {

    private lateinit var database: AppDatabase

    private val user = DbUser("+1234555876")

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() = runBlockingTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        database.userDao().insertUser(user) // to pass foreign key constraint
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndFetchUserDetailByPhoneNumber() = runBlockingTest {
        val userDetail = DbUserDetail(user.phoneNumber, "John", "Doe", "https://example.com")
        database.userDetailDao().insertUserDetail(userDetail)

        val loaded = database.userDetailDao().getUserDetailByPhoneNumber(userDetail.phoneNumber)
        assertThat(loaded, `is`(userDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetUserDetailsOfSpecifiedPhoneNumbers() = runBlockingTest {
        val userDetail = DbUserDetail(user.phoneNumber, "John", "Doe", "https://example.com")
        database.userDetailDao().insertUserDetail(userDetail)

        val loaded =
            database.userDetailDao().getUserDetailsByPhoneNumbers(listOf(userDetail.phoneNumber))
        assertThat(loaded[0], `is`(userDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertingDetailAgainForSameUserReplacesPreviousDetail() = runBlockingTest {
        val oldUserDetail = DbUserDetail(user.phoneNumber, "John", "Doe", "https://example.com")
        database.userDetailDao().insertUserDetail(oldUserDetail)   // first insert

        val newUserDetail = oldUserDetail.copy(firstName = "Jane")
        database.userDetailDao().insertUserDetail(newUserDetail)   // second insert

        val loaded = database.userDetailDao().getUserDetailByPhoneNumber(oldUserDetail.phoneNumber)
        assertThat(loaded, `is`(notNullValue()))
        assertThat(loaded, `is`(not(oldUserDetail)))
        assertThat(loaded, `is`(newUserDetail))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingUserDeletesUserDetail() = runBlockingTest {
        val userDetail = DbUserDetail(user.phoneNumber, "John", "Doe", "https://example.com")
        database.userDetailDao().insertUserDetail(userDetail)

        database.userDao().deleteUser(user)

        val loaded = database.userDetailDao().getUserDetailByPhoneNumber(userDetail.phoneNumber)
        assertThat(loaded, `is`(nullValue()))
    }
}