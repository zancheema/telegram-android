package com.zancheema.android.telegram.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.data.source.local.AppDatabase
import com.zancheema.android.telegram.data.source.local.entity.DbChatRoom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatRoomDaoTest {

    private lateinit var database: AppDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetAllChatRooms() = runBlockingTest {
        val chatRooms = listOf(
            DbChatRoom("chat_room_1"),
            DbChatRoom("chat_room_2"),
            DbChatRoom("chat_room_3")
        )
        chatRooms.forEach { chatRoom ->
            database.chatRoomDao().insertChatRoom(chatRoom)
        }

        val loaded = database.chatRoomDao().getAllChatRooms()
        assertThat(loaded.size, `is`(chatRooms.size))
        assertThat(loaded, `is`(chatRooms))
    }

    @Test
    fun chatRoomAlreadyExists_ReinsertionIsIgnored() = runBlockingTest {
        val chatRoom1 = DbChatRoom("chat_room_1")
        database.chatRoomDao().insertChatRoom(chatRoom1) // first time
        val chatRoom2 = DbChatRoom("chat_room_1")
        database.chatRoomDao().insertChatRoom(chatRoom2) // first time

        val loaded = database.chatRoomDao().getAllChatRooms()
        assertThat(loaded.size, `is`(1))
        assertThat(loaded[0], `is`(chatRoom1))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoom_DeletesChatRoomFromAllChatRooms() = runBlockingTest {
        val chatRoom = DbChatRoom("chat_room")
        database.chatRoomDao().insertChatRoom(chatRoom)

        database.chatRoomDao().deleteChatRoom(chatRoom)

        val allChatRooms = database.chatRoomDao().getAllChatRooms()
        assertThat(allChatRooms.size, `is`(0))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllChatRooms() = runBlockingTest {
        val chatRooms = listOf(
            DbChatRoom("chat_room_1"),
            DbChatRoom("chat_room_2"),
            DbChatRoom("chat_room_3")
        )
        chatRooms.forEach { chatRoom ->
            database.chatRoomDao().insertChatRoom(chatRoom)
        }

        database.chatRoomDao().deleteAllChatRooms()

        val loaded = database.chatRoomDao().getAllChatRooms()
        assertThat(loaded.size, `is`(0))
    }
}