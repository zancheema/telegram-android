package com.zancheema.android.telegram.chats

import com.zancheema.android.telegram.data.source.FakeRepository
import com.zancheema.android.telegram.data.source.domain.Chat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class ChatsViewModelTest {
    private lateinit var repository: FakeRepository
    private lateinit var viewModel: ChatsViewModel

    @Before
    fun init() {
        repository = FakeRepository()
        viewModel = ChatsViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun chatsReturnChatList() = runBlockingTest {
        val chats = listOf(
            Chat("cr_1", "http://example.com", "name", "+1335", ""),
            Chat("cr_2", "http://example.com", "name2", "+133785", "")
        )
        repository.setChats(chats)

        assertThat(viewModel.chats.first(), `is`(chats))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun emptyChatsGeneratesEmptyChatsEvent() = runBlockingTest {
        assertThat(viewModel.chats.first(), `is`(emptyList()))
        val event = viewModel.emptyChatsEvent.first()
        assertThat(event.getContentIfNotHandled(), `is`(true))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun openChatGeneratesOpenChatEvent() = runBlockingTest {
        // Event is null by default
        var event = viewModel.openChatsEvent.first()
        assertThat(event?.getContentIfNotHandled(), `is`(nullValue()))

        // Call openChat(Chat)
        val chat = Chat("cr_1", "http://example.com", "John", "+134", "hey")
        viewModel.openChat(chat)

        // Event is generated
        event = viewModel.openChatsEvent.first()
        assertThat(event?.getContentIfNotHandled(), `is`(chat))
    }
}