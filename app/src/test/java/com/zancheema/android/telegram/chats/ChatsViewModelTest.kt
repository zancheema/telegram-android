package com.zancheema.android.telegram.chats

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zancheema.android.telegram.MainCoroutineRule
import com.zancheema.android.telegram.getOrAwaitValue
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatsViewModelTest {
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: ChatsViewModel

    private val me = UserDetail("12345674389", "Jane", "Doe", msgToken = "other_token")
    private val otherUser1 = UserDetail("12345676347", "John", "Doe", msgToken = "other_token")
    private val otherUser2 =
        UserDetail("12345656381", "Dwayne", "Johnson", msgToken = "other_token")
    private val msg1 = ChatMessage("Hey!", otherUser1, me)
    private val msg2 = ChatMessage("Hi!", me, otherUser1)
    private val msg3 = ChatMessage("Lets grab some coffee", otherUser1, me)
    private val msg4 = ChatMessage("Alright", me, otherUser1)
    private val msg5 = ChatMessage("Great!", otherUser1, me)
    private val msg6 = ChatMessage("Hello!", otherUser2, me)
    private val allMessages = listOf(
        msg1, msg2, msg3, msg4, msg5, msg6
    )

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() = runBlocking {
        repository = FakeRepository()
        repository.saveUserDetail(me)
        allMessages.forEach { repository.saveMessage(it) }
        viewModel = ChatsViewModel(repository)
    }

    @Test
    fun chatHeads_ReturnChatHeadsWithRecentChatMessages() {
        val chatHeads = viewModel.chats.getOrAwaitValue()
        assertThat(chatHeads.size, `is`(2))
        assertThat(chatHeads[0].name, `is`(msg5.from.fullName))
        assertThat(chatHeads[0].recentMessage, `is`(msg5.message))
        assertThat(chatHeads[1].name, `is`(msg6.from.fullName))
        assertThat(chatHeads[1].recentMessage, `is`(msg6.message))
    }
}