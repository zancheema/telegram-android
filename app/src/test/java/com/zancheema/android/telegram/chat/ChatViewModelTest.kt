package com.zancheema.android.telegram.chat

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
class ChatViewModelTest {
    private lateinit var viewModel: ChatViewModel
    private lateinit var repository: FakeRepository

    private val sender = UserDetail("+13434589734", "John", "Doe")
    private val receiver = UserDetail("+32579459634", "John", "Doe")
    private val chatRoom = ChatRoom("chat_room", receiver.phoneNumber)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() = runBlockingTest {
        repository = FakeRepository()
        viewModel = ChatViewModel(repository)

        repository.apply {
            saveUser(User(sender.phoneNumber))
            saveUser(User(receiver.phoneNumber))

            saveUserDetail(sender)
            saveUserDetail(receiver)

            saveChatRoom(chatRoom)
        }
    }

    @ExperimentalCoroutinesApi
    @After
    fun cleanUp() = runBlockingTest {
        repository.apply {
            deleteAllUsers()
            deleteAllUserDetails()
            deleteAllChatRooms()
            deleteAllChatMessages()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testPreconditions() = runBlockingTest {
        val userDetails = repository.observeUserDetails().first()
        assertThat((userDetails as Success).data, `is`(listOf(sender, receiver)))

        val chatMessages = repository.observeChatMessages().first()
        assertThat((chatMessages as Success).data, `is`(emptyList()))
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun messages_ChatIsNull_GeneratesInvalidChatEventAndReturnsChatMessagesAsEmptyList() =
        runBlockingTest {
            val messages = listOf(
                ChatMessage("msg_1", chatRoom.id, "Hello"),
                ChatMessage("msg_1", chatRoom.id, "Hey", isMine = false),
                ChatMessage("msg_1", chatRoom.id, "How's it going?")
            )
            messages.forEach { repository.saveChatMessage(it) }

            val loaded = viewModel.chatMessages.first()
            assertThat(loaded, `is`(emptyList()))
            assertThat(
                viewModel.invalidChatEvent.first().getContentIfNotHandled(),
                `is`(R.string.invalid_chat)
            )
        }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun messages_ChatNotNull_ReturnsChatMessagesWithChatRoomId() = runBlockingTest {
        viewModel.setChatRoomId(chatRoom.id)

        val messages = listOf(
            ChatMessage("msg_1", chatRoom.id, "Hello"),
            ChatMessage("msg_2", chatRoom.id, "Hey", isMine = false),
            ChatMessage("msg_3", chatRoom.id, "How's it going?")
        )
        messages.forEach { repository.saveChatMessage(it) }

        val loaded = viewModel.chatMessages.first()
        assertThat(loaded, `is`(messages))
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @Test
    fun sendingNewMessageAddsChatMessageToMessages() = runBlockingTest {
        val message = "Hello"
        viewModel.setChatRoomId(chatRoom.id)
        viewModel.messageText.value = message
        viewModel.sendMessage()

        val loaded = viewModel.chatMessages.first()
        assertThat(loaded.size, `is`(1))
        assertThat(loaded[0].message, `is`(message))
        assertThat(loaded[0].chatRoomId, `is`(chatRoom.id))
        assertThat(loaded[0].isMine, `is`(true))
    }
}