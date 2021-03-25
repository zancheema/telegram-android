package com.zancheema.android.telegram.chat

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.ChatMessage
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.source.saveMessageBlocking
import com.zancheema.android.telegram.source.saveUserDetailBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(AppRepositoryModule::class)
@HiltAndroidTest
class ChatFragmentTest {

    private val me = UserDetail("12345674389", "Jane", "Doe", msgToken = "other_token")
    private val otherUser = UserDetail("12345676347", "John", "Doe", msgToken = "other_token")
    private val unrelatedUser =
        UserDetail("12345656381", "Dwayne", "Johnson", msgToken = "other_token")
    private val msg1 = ChatMessage("Hey!", otherUser, me)
    private val msg2 = ChatMessage("Hi!", me, otherUser, isMine = true)
    private val msg3 = ChatMessage("Lets grab some coffee", otherUser, me)
    private val msg4 = ChatMessage("Alright", me, otherUser, isMine = true)
    private val msg5 = ChatMessage("Great!", otherUser, me)
    private val msg6 = ChatMessage("Hello!", unrelatedUser, me)
    private val allMessages = listOf(
        msg1, msg2, msg3, msg4, msg5, msg6
    )
    private val relatedMessages = listOf(
        msg1, msg2, msg3, msg4, msg5
    )
    private val unrelatedMessages = listOf(msg6)

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: AppRepository

    @Before
    fun init() {
        hiltRule.inject()
        repository.saveUserDetailBlocking(me)
        allMessages.forEach {
            repository.saveMessageBlocking(it)
        }
    }

    @Test
    fun openingChatWithNoUser_ShowsNoChatAndDoesNotThrowAnyException() {
        val bundle = Bundle()
        bundle.putParcelable("otherUserDetail", otherUser)
        launchFragmentInHiltContainer<ChatFragment>(bundle, R.style.Theme_Telegram)
    }

    @Test
    fun messageListShowsChatMessagesWithOtherSingleUser() {
        val bundle = Bundle()
        bundle.putParcelable("otherUserDetail", otherUser)
        launchFragmentInHiltContainer<ChatFragment>(bundle, R.style.Theme_Telegram)

        for (m in relatedMessages) {
            onView(withText(m.message)).check(matches(isDisplayed()))
        }
        for (m in unrelatedMessages) {
            onView(withText(m.message)).check(doesNotExist())
        }
    }

    @Test
    fun sendBlankMessage_MessageNotSent() {
        val bundle = Bundle()
        bundle.putParcelable("otherUserDetail", otherUser)
        launchFragmentInHiltContainer<ChatFragment>(bundle, R.style.Theme_Telegram)

        val newMessage = "How about 9am? "
        onView(withText(newMessage)).check(doesNotExist())
        onView(withId(R.id.writeMessageText))
            .perform(typeText(newMessage), closeSoftKeyboard())
        onView(withId(R.id.sendMessageButton))
            .perform(click())
        onView(withId(R.id.writeMessageText))
            .check(matches(withText("")))
        onView(withId(R.id.messageList))
            .check(matches(hasDescendant(withText(newMessage))))
    }

    @Test
    fun sendNewMessage_ShowsInChat() {
        val bundle = Bundle()
        bundle.putParcelable("otherUserDetail", otherUser)
        launchFragmentInHiltContainer<ChatFragment>(bundle, R.style.Theme_Telegram)

        val newMessage = "How about 9am? "
        onView(withText(newMessage)).check(doesNotExist())
        onView(withId(R.id.writeMessageText))
            .perform(typeText(newMessage), closeSoftKeyboard())
        onView(withId(R.id.sendMessageButton))
            .perform(click())
        onView(withId(R.id.writeMessageText))
            .check(matches(withText("")))
        onView(withId(R.id.messageList))
            .check(matches(hasDescendant(withText(newMessage))))
    }
}