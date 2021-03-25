package com.zancheema.android.telegram.local.dao

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
import java.lang.System.currentTimeMillis

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatMessageDaoTest {

    private lateinit var database: AppDatabase
    private val chatRoom = DbChatRoom("chat_room")
    private val user1 = DbUser("+14375559081")
    private val user2 = DbUser("+134375559820")

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
        database.apply {
            chatRoomDao().insertChatRoom(chatRoom)
            userDao().insertUser(user1)
            userDao().insertUser(user2)
        }
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun insertAndGetChatMessageById() = runBlockingTest {
        val message1 = DbChatMessage(
            "cm_1",
            chatRoom.id,
            user1.phoneNumber,
            user2.phoneNumber,
            currentTimeMillis(),
            "Hey"
        )
        val message2 = DbChatMessage(
            "cm_2",
            chatRoom.id,
            user2.phoneNumber,
            user1.phoneNumber,
            currentTimeMillis() + 23,
            "Hello"
        )
        database.chatMessageDao().apply {
            insertChatMessage(message1)
            insertChatMessage(message2)
        }

        val loaded = database.chatMessageDao().getChatMessageById(message2.id)
        assertThat(loaded, `is`(message2))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatMessagesByChatRoomId() = runBlockingTest {
        val messages = listOf(
            DbChatMessage(
                "cm_1",
                chatRoom.id,
                user1.phoneNumber,
                user2.phoneNumber,
                currentTimeMillis(),
                "Hey"
            ),
            DbChatMessage(
                "cm_2",
                chatRoom.id,
                user2.phoneNumber,
                user1.phoneNumber,
                currentTimeMillis() + 23,
                "Hello"
            )
        )
        for (m in messages) database.chatMessageDao().insertChatMessage(m)

        val loaded = database.chatMessageDao().getChatMessagesByChatRoomId(chatRoom.id)
        assertThat(loaded, `is`(messages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deletingChatRoomDeletesAllChatMessagesWithThatChatRoomId() = runBlockingTest {
        // insert another chat room to ensure that
        // the call only fetches messages with the given chat room id
        val secondChatRoom = DbChatRoom("chat_room_2")
        database.chatRoomDao().insertChatRoom(secondChatRoom)
        val messages = listOf(
            DbChatMessage(
                "cm_1",
                chatRoom.id,
                user1.phoneNumber,
                user2.phoneNumber,
                currentTimeMillis(),
                "Hey"
            ),
            DbChatMessage(
                "cm_2",
                chatRoom.id,
                user2.phoneNumber,
                user1.phoneNumber,
                currentTimeMillis() + 23,
                "Hello"
            ),
            DbChatMessage(
                "cm_3",
                secondChatRoom.id,
                user1.phoneNumber,
                user2.phoneNumber,
                currentTimeMillis() + 23,
                "Hi"
            )
        )
        for (m in messages) database.chatMessageDao().insertChatMessage(m)

        database.chatRoomDao().deleteChatRoom(chatRoom)

        val loaded = database.chatMessageDao().getChatMessagesByChatRoomId(chatRoom.id)
        assertThat(loaded.size, `is`(0))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageById_GetByIdReturnsNull() = runBlockingTest {
        val message = DbChatMessage(
            "chat_message",
            chatRoom.id,
            user1.phoneNumber,
            user2.phoneNumber,
            currentTimeMillis(),
            "Hello"
        )
        database.chatMessageDao().insertChatMessage(message)

        database.chatMessageDao().deleteChatMessage(message)

        val loaded = database.chatMessageDao().getChatMessageById(message.id)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatRoomChatMessages_GetChatMessagesByChatRoomIdReturnsEmptyList() = runBlockingTest {
        val messages = listOf(
            DbChatMessage(
                "cm_1",
                chatRoom.id,
                user1.phoneNumber,
                user2.phoneNumber,
                currentTimeMillis(),
                "Hey"
            ),
            DbChatMessage(
                "cm_2",
                chatRoom.id,
                user2.phoneNumber,
                user1.phoneNumber,
                currentTimeMillis() + 23,
                "Hello"
            )
        )
        for (m in messages) database.chatMessageDao().insertChatMessage(m)

        database.chatMessageDao().deleteChatMessagesByChatRoomId(chatRoom.id)

        val loaded = database.chatMessageDao().getChatMessagesByChatRoomId(chatRoom.id)
        assertThat(loaded.isEmpty(), `is`(true))
    }
}