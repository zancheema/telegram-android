package com.zancheema.android.telegram.chat

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.ChatRoom
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.source.saveChatRoomBlocking
import com.zancheema.android.telegram.source.saveUserBlocking
import com.zancheema.android.telegram.source.saveUserDetailBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppRepositoryModule::class)
@HiltAndroidTest
class ChatFragmentTest {

    private val sender = UserDetail("+13434589734", "John", "Doe")
    private val receiver = UserDetail("+32579459634", "John", "Doe")
    private val chat =
        Chat("chat_room", "http://example.com", receiver.firstName, receiver.phoneNumber)

    @Inject
    lateinit var repository: FakeRepository

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()

        repository.apply {
            saveUserBlocking(User(sender.phoneNumber))
            saveUserBlocking(User(receiver.phoneNumber))

            saveUserDetailBlocking(sender)
            saveUserDetailBlocking(receiver)

            saveChatRoomBlocking(ChatRoom(chat.chatRoomId, chat.phoneNumber))
        }
    }

    @Test
    fun fragmentLaunchesSuccessfully() {
        val args = ChatFragmentArgs(chat)
        launchFragmentInHiltContainer<ChatFragment>(args.toBundle(), R.style.Theme_Telegram)

        onView(withId(R.id.messageList))
            .check(matches(isDisplayed()))
    }
}