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
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ChatMessageDaoTest {

    private lateinit var database: AppDatabase

    private val user1 = DbUser("+13425559024")
    private val user2 = DbUser("+143425550013")
    private val chatRoom1 = DbChatRoom("chat_room_1", user1.phoneNumber)
    private val chatRoom2 = DbChatRoom("chat_room_2", user2.phoneNumber)
    private val users = listOf(user1, user2)
    private val chatRooms = listOf(chatRoom1, chatRoom2)

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
        chatRooms.forEach { database.chatRoomDao().insert(it) }
    }

    @After
    fun closeDb() = database.close()

    @ExperimentalCoroutinesApi
    @Test
    fun testPreconditions() = runBlockingTest {
        assertThat(database.userDao().getAll(), `is`(users))
        assertThat(database.chatRoomDao().getAll(), `is`(chatRooms))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatMessagesAndGetAll() = runBlockingTest {
        val messages = listOf(
            DbChatMessage("msg_1", chatRoom1.id, "Hello"),
            DbChatMessage("msg_2", chatRoom1.id, "How are you doing?")
        )
        messages.forEach { database.chatMessageDao().insert(it) }

        val loaded = database.chatMessageDao().getAll()
        assertThat(loaded, `is`(messages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatMessagesAndGetByChatRoomId() = runBlockingTest {
        val chatRoom1Messages = listOf(
            DbChatMessage("msg_1", chatRoom1.id, "Hello"),
            DbChatMessage("msg_2", chatRoom1.id, "How are you doing?")
        )
        val chatRoom2Messages = listOf(
            DbChatMessage("msg_3", chatRoom2.id, "Hello"),
            DbChatMessage("msg_4", chatRoom2.id, "Hi!", false)
        )
        for (c in chatRoom1Messages) database.chatMessageDao().insert(c)
        for (c in chatRoom2Messages) database.chatMessageDao().insert(c)

        val loaded = database.chatMessageDao().getByChatRoomId(chatRoom1.id)
        assertThat(loaded, `is`(chatRoom1Messages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertChatMessageAndGetById() = runBlockingTest {
        val messages = listOf(
            DbChatMessage("msg_1", chatRoom1.id, "Hello"),
            DbChatMessage("msg_2", chatRoom1.id, "How are you doing?")
        )
        messages.forEach { database.chatMessageDao().insert(it) }

        val loaded = database.chatMessageDao().getById(messages[0].id)
        assertThat(loaded, `is`(messages[0]))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun insertingSameChatMessageTwiceReplaceFirstChatMessage() = runBlockingTest {
        val chatMessage = DbChatMessage("msg_1", chatRoom1.id, "Hello")
        database.chatMessageDao().insert(chatMessage)

        val updatedChatMessage = chatMessage.copy(message = "Hey")
        database.chatMessageDao().insert(updatedChatMessage)

        val loaded = database.chatMessageDao().getById(chatMessage.id)
        assertThat(loaded, `is`(not(chatMessage)))
        assertThat(loaded, `is`(updatedChatMessage))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteAllInsertedChatMessagesAndGetAll_ReturnsEmptyList() = runBlockingTest {
        val messages = listOf(
            DbChatMessage("msg_1", chatRoom1.id, "Hello"),
            DbChatMessage("msg_2", chatRoom1.id, "How are you doing?")
        )
        messages.forEach { database.chatMessageDao().insert(it) }

        database.chatMessageDao().deleteAll()

        val loaded = database.chatMessageDao().getAll()
        assertThat(loaded, `is`(emptyList()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessagesByChatRoomIdAndGetByTheChatRoomId_ReturnsEmptyList() = runBlockingTest {
        val chatRoom1Messages = listOf(
            DbChatMessage("msg_1", chatRoom1.id, "Hello"),
            DbChatMessage("msg_2", chatRoom1.id, "How are you doing?")
        )
        val chatRoom2Messages = listOf(
            DbChatMessage("msg_3", chatRoom2.id, "Hello"),
            DbChatMessage("msg_4", chatRoom2.id, "Hi!", false)
        )
        for (c in chatRoom1Messages) database.chatMessageDao().insert(c)
        for (c in chatRoom2Messages) database.chatMessageDao().insert(c)

        database.chatMessageDao().deleteByChatRoomId(chatRoom1.id)

        var loaded = database.chatMessageDao().getByChatRoomId(chatRoom1.id)
        assertThat(loaded, `is`(emptyList()))
        loaded = database.chatMessageDao().getByChatRoomId(chatRoom2.id)
        assertThat(loaded, `is`(chatRoom2Messages))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageAndGetById_ReturnsNullValue() = runBlockingTest {
        val chatMessage = DbChatMessage("msg_1", chatRoom1.id, "Hello")
        database.chatMessageDao().insert(chatMessage)

        database.chatMessageDao().delete(chatMessage)

        val loaded = database.chatMessageDao().getById(chatMessage.id)
        assertThat(loaded, `is`(nullValue()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteChatMessageById_ReturnsNullValue() = runBlockingTest {
        val chatMessage = DbChatMessage("msg_1", chatRoom1.id, "Hello")
        database.chatMessageDao().insert(chatMessage)

        database.chatMessageDao().deleteById(chatMessage.id)

        val loaded = database.chatMessageDao().getById(chatMessage.id)
        assertThat(loaded, `is`(nullValue()))
    }
}