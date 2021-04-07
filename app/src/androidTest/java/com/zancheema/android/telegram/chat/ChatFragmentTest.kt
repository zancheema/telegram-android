package com.zancheema.android.telegram.chat

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.domain.*
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.source.*
import com.zancheema.android.telegram.util.saveChatMessageBlocking
import com.zancheema.android.telegram.util.saveChatRoomBlocking
import com.zancheema.android.telegram.util.saveUserBlocking
import com.zancheema.android.telegram.util.saveUserDetailBlocking
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
        launchChat()

        onView(withId(R.id.messageList))
            .check(matches(isDisplayed()))
    }

    @Test
    fun toolbarDisplaysSenderName() {
        launchChat()

        onView(withId(R.id.tvTitle))
            .check(matches(withText(sender.firstName)))
    }

    @Test
    fun typeAndSendMessage_MessageIsDisplayedInMessageList() {
        launchChat()
        val message = "Hey there!"

        onView(withId(R.id.etMessage))
            .perform(typeText(message), closeSoftKeyboard())
        onView(withId(R.id.fabSendMessage))
            .perform(click())

        onView(withId(R.id.messageList))
            .check(matches(hasDescendant(withText(message))))
        onView(withId(R.id.etMessage))
            .check(matches(withText("")))
    }

    @Test
    fun receivedMessageIsShownInMessageList() {
        launchChat()

        // new message is received from outside
        val chatMessage = ChatMessage("msg_1", chat.chatRoomId, "Hello", false)
        repository.saveChatMessageBlocking(chatMessage)

        onView(withId(R.id.messageList))
            .check(matches(hasDescendant(withText(chatMessage.message))))
    }

    private fun launchChat() {
        val args = ChatFragmentArgs(chat)
        launchFragmentInHiltContainer<ChatFragment>(args.toBundle(), R.style.Theme_Telegram)
    }
}