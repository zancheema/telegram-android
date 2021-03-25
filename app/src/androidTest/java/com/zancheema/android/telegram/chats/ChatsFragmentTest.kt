package com.zancheema.android.telegram.chats

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
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
class ChatsFragmentTest {

    private val me = UserDetail("12345674389", "Jane", "Doe", msgToken = "other_token")
    private val otherUser1 = UserDetail("12345676347", "John", "Doe", msgToken = "other_token")
    private val otherUser2 =
        UserDetail("12345656381", "Dwayne", "Johnson", msgToken = "other_token")
    private val msg1 = ChatMessage("Hey!", otherUser1, me)
    private val msg2 = ChatMessage("Hi!", me, otherUser1, isMine = true)
    private val msg3 = ChatMessage("Lets grab some coffee", otherUser1, me)
    private val msg4 = ChatMessage("Alright", me, otherUser1, isMine = true)
    private val msg5 = ChatMessage("Great!", otherUser1, me) // most recent 1
    private val msg6 = ChatMessage("Hello!", otherUser2, me) // most recent 2
    private val allMessages = listOf(
        msg1, msg2, msg3, msg4, msg5, msg6
    )

    @Inject
    lateinit var repository: AppRepository

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        repository.saveUserDetailBlocking(me)
        allMessages.forEach {
            repository.saveMessageBlocking(it)
        }
    }

    @Test
    fun chatList_ShowsChatHeadsWithRecentChats() {
        launchFragmentInHiltContainer<ChatsFragment>(Bundle(), R.style.Theme_Telegram)

        onView(withText(msg1.message)).check(doesNotExist())
        onView(withText(msg2.message)).check(doesNotExist())
        onView(withText(msg3.message)).check(doesNotExist())
        onView(withText(msg4.message)).check(doesNotExist())
        onView(withId(R.id.chatsList)).apply {
            check(matches(isDisplayed()))
            check(matches(hasDescendant(withText(msg5.from.fullName))))
            check(matches(hasDescendant(withText(msg6.message))))
            check(matches(hasDescendant(withText(msg6.from.fullName))))
            check(matches(hasDescendant(withText(msg5.message))))
        }
    }
}