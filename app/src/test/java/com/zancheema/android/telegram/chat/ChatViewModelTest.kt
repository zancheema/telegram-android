package com.zancheema.android.telegram.chat

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.getOrAwaitValue
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.source.saveMessageBlocking
import com.zancheema.android.telegram.source.saveUserDetailBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatViewModelTest {
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: ChatViewModel

    private val me = UserDetail("12345674389", "Jane", "Doe", msgToken = "other_token")
    private val otherUser = UserDetail("12345676347", "John", "Doe", msgToken = "other_token")
    private val unrelatedUser =
        UserDetail("12345656381", "Dwayne", "Johnson", msgToken = "other_token")
    private val msg1 = ChatMessage("Hey!", otherUser, me)
    private val msg2 = ChatMessage("Hi!", me, otherUser)
    private val msg3 = ChatMessage("Lets grab some coffee", otherUser, me)
    private val msg4 = ChatMessage("Alright", me, otherUser)
    private val msg5 = ChatMessage("Great!", otherUser, me)
    private val msg6 = ChatMessage("Hello!", unrelatedUser, me)
    private val allMessages = listOf(
        msg1, msg2, msg3, msg4, msg5, msg6
    )
    private val relatedMessages = listOf(
        msg1, msg2, msg3, msg4, msg5
    )

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        repository = FakeRepository()
        repository.saveUserDetailBlocking(me)
        allMessages.forEach { repository.saveMessageBlocking(it) }
        viewModel = ChatViewModel(repository)
        viewModel.setOtherUserDetail(otherUser)
    }

    @Test
    fun getMessagesFetchesRelatedMessages() {
        val messages = viewModel.messages.getOrAwaitValue()
        assertThat(messages, `is`(relatedMessages))
        // doesn't contain unrelated messages
        assertThat(messages.contains(msg6), `is`(false))
    }

    @Test
    fun sendBlankMessage_MessageIsNotSent() {
        // Given: Blank message
        val newMessage = "  "
        viewModel.newMessage.value = newMessage
        viewModel.sendMessage()

        val messages: List<String> = viewModel.messages.getOrAwaitValue().map { it.message }
        assertThat(messages.contains(newMessage), `is`(false))
        // viewMessage is cleared
        assertThat(viewModel.newMessage.getOrAwaitValue().isEmpty(), `is`(true))
    }

    @Test
    fun sendMessage_SavesNewMessageSentByMe() {
        val newMessage = "How about 9am?"
        viewModel.newMessage.value = newMessage
        viewModel.sendMessage()

        val messages = viewModel.messages.getOrAwaitValue()
        val msg = messages.firstOrNull { it.message == newMessage }
        assertThat(msg, `is`(notNullValue()))
        msg as ChatMessage
        assertThat(msg.isMine, `is`(true))
        assertThat(viewModel.newMessage.getOrAwaitValue().isEmpty(), `is`(true))
    }
}