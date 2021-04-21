package com.zancheema.android.telegram.source.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.entity.DbChatMessage
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import com.zancheema.android.telegram.data.source.local.entity.DbUser
import com.zancheema.android.telegram.data.source.local.entity.DbUserDetail
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
    fun getAllChatsReturnsChatsForChatRoomsWithMinimumOneMessage() = runBlockingTest {
        val userDetail1 = DbUserDetail(user1.phoneNumber, "John", "Doe", "http://example.com")
        val userDetail2 = DbUserDetail(user2.phoneNumber, "John", "Doe", "http://example.com")
        val userDetail3 = DbUserDetail(user3.phoneNumber, "Michael", "Doe", "http://example.com")
        val userDetails = listOf(userDetail1, userDetail2, userDetail3)
        userDetails.forEach { database.userDetailDao().insertUserDetail(it) }

        val chatRoom1 = DbChatRoom("chat_room_1", user1.phoneNumber)
        val chatRoom2 = DbChatRoom("chat_room_2", user2.phoneNumber)
        val chatRoom3 = DbChatRoom("chat_room_3", user3.phoneNumber)
        val chatRooms = listOf(
            chatRoom1,
            chatRoom2,
            chatRoom3
        )
        chatRooms.forEach { database.chatRoomDao().insert(it) }

        val time = System.currentTimeMillis()
        val chatRoom1Message1 = DbChatMessage("m1", chatRoom1.id, "Hello", timestamp = time)
        val chatRoom1Message2 =
            DbChatMessage("m2", chatRoom1.id, "How are you doing?", timestamp = time + 2L)
        val chatRoom2Message1 =
            DbChatMessage("m3", chatRoom2.id, "Hey", false, timestamp = time + 1L)
        val messages = listOf(chatRoom1Message1, chatRoom1Message2, chatRoom2Message1)
        messages.forEach { database.chatMessageDao().insert(it) }

        val loaded = database.chatRoomDao().getAllChats()
        assertThat(loaded.size, `is`(2))

        val chat1 = loaded[0]
        assertThat(chat1.chatRoomId, `is`(chatRoom1.id))
        assertThat(chat1.phoneNumber, `is`(chatRoom1.phoneNumber))
        assertThat(chat1.photoUrl, `is`(userDetail1.photoUrl))
        assertThat(chat1.userName, `is`(userDetail1.firstName))
        assertThat(chat1.recentMessage, `is`(chatRoom1Message2.message))
        assertThat(chat1.timestamp, `is`(chatRoom1Message2.timestamp))

        val chat2 = loaded[1]
        assertThat(chat2.chatRoomId, `is`(chatRoom2.id))
        assertThat(chat2.phoneNumber, `is`(chatRoom2.phoneNumber))
        assertThat(chat2.photoUrl, `is`(userDetail2.photoUrl))
        assertThat(chat2.userName, `is`(userDetail2.firstName))
        assertThat(chat2.recentMessage, `is`(chatRoom2Message1.message))
        assertThat(chat2.timestamp, `is`(chatRoom2Message1.timestamp))
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