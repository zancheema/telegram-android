package com.zancheema.android.telegram.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import com.zancheema.android.telegram.data.source.local.entity.DbUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatRoomDaoTest {

    private lateinit var database: AppDatabase

    // phone number inserted pre-testing to meet
    // foreign key constraints
    private val user1 = DbUser("+12345556732")
    private val user2 = DbUser("+12345556764")
    private val user3 = DbUser("+193445556732")
    private val users = listOf(user1, user2, user3)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun initDb() = runBlockingTest {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        users.forEach { database.userDao().insertUser(it) }
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun testPreconditions() = runBlockingTest {
        assertThat(database.chatRoomDao().getAll(), `is`(emptyList()))
        assertThat(database.userDao().getAll(), `is`(users))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatRoomsAndGetAll() = runBlockingTest {
        val chatRooms = listOf(
            DbChatRoom("chat_room_1", user1.phoneNumber),
            DbChatRoom("chat_room_2", user2.phoneNumber),
            DbChatRoom("chat_room_3", user3.phoneNumber)
        )
        chatRooms.forEach { database.chatRoomDao().insert(it) }

        val loaded = database.chatRoomDao().getAll()
        assertThat(loaded, `is`(chatRooms))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatRoomAndGetById() = runBlockingTest {
        val chatRoom = DbChatRoom("chat_room_1", user1.phoneNumber)
        database.chatRoomDao().insert(chatRoom)

        val loaded = database.chatRoomDao().getById(chatRoom.id)
        assertThat(loaded, `is`(chatRoom))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertingSameChatRoomAgainIsIgnored() = runBlockingTest {
        val chatRoom = DbChatRoom("chat_room_1", user1.phoneNumber)
        database.chatRoomDao().insert(chatRoom)

        val updateChatRoom = chatRoom.copy(phoneNumber = user2.phoneNumber)
        database.chatRoomDao().insert(updateChatRoom)

        val loaded = database.chatRoomDao().getById(chatRoom.id)
        assertThat(loaded, `is`(not(updateChatRoom)))
        assertThat(loaded, `is`(chatRoom))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllInsertedChatRoomAndGetAll_ReturnsEmptyList() = runBlockingTest {
        val chatRooms = listOf(
            DbChatRoom("chat_room_1", user1.phoneNumber),
            DbChatRoom("chat_room_2", user2.phoneNumber),
            DbChatRoom("chat_room_3", user3.phoneNumber)
        )
        chatRooms.forEach { database.chatRoomDao().insert(it) }

        database.chatRoomDao().deleteAll()

        val loaded = database.chatRoomDao().getAll()
        assertThat(loaded, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomAndGetById_ReturnsNullValue() = runBlockingTest {
        val chatRoom = DbChatRoom("chat_room_1", user1.phoneNumber)
        database.chatRoomDao().insert(chatRoom)

        database.chatRoomDao().delete(chatRoom)

        val loaded = database.chatRoomDao().getById(chatRoom.id)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomByIdAndGetById_ReturnsNullValue() = runBlockingTest {
        val chatRoom = DbChatRoom("chat_room_1", user1.phoneNumber)
        database.chatRoomDao().insert(chatRoom)

        database.chatRoomDao().deleteById(chatRoom.id)

        val loaded = database.chatRoomDao().getById(chatRoom.id)
        assertThat(loaded, `is`(nullValue()))
    }
}