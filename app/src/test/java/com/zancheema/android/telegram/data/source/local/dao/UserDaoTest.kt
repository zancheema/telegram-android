package com.zancheema.android.telegram.data.source.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.dao.UserDao
import com.zancheema.android.telegram.data.source.local.entity.DbUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class UserDaoTest {

    private lateinit var database: AppDatabase

    /**
     * [UserDao]'s (class under test) object
     */
    private lateinit var userDao: UserDao

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun insertUsersAndGetAll() = runBlockingTest {
        val users = listOf(
            DbUser("+12345556732"),
            DbUser("+12345556764"),
            DbUser("+193445556732")
        )
        users.forEach { database.userDao().insertUser(it) }

        val loaded = database.userDao().getAll()
        assertThat(loaded, `is`(users))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertUserAndGetByPhoneNumber() = runBlockingTest {
        val user = DbUser("+15465558912")
        database.userDao().insertUser(user)

        val loaded = database.userDao().getUserByPhoneNumber(user.phoneNumber)
        assertThat(loaded, `is`(user))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllInsertedUsersAndGetAll_ReturnsEmptyList() = runBlockingTest {
        val users = listOf(
            DbUser("+12345556732"),
            DbUser("+12345556764"),
            DbUser("+193445556732")
        )
        users.forEach { database.userDao().insertUser(it) }

        database.userDao().deleteAll()

        val loaded = database.userDao().getAll()
        assertThat(loaded, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUser_getUserByPhoneNumberReturnsNull() = runBlockingTest {
        val user = DbUser("+15465558912")
        database.userDao().insertUser(user)

        database.userDao().deleteUser(user)

        val loaded = database.userDao().getUserByPhoneNumber(user.phoneNumber)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserByPhoneNumber_getUserByPhoneNumberReturnsNull() = runBlockingTest {
        val user = DbUser("+15465558912")
        database.userDao().insertUser(user)

        database.userDao().deleteUserByPhoneNumber(user.phoneNumber)

        val loaded = database.userDao().getUserByPhoneNumber(user.phoneNumber)
        assertThat(loaded, `is`(nullValue()))
    }
}