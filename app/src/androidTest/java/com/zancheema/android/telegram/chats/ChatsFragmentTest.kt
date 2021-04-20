package com.zancheema.android.telegram.chats

import android.os.Bundle
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppContentModule
import com.zancheema.android.telegram.getTestNavController
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.util.saveUserDetailBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppContentModule::class)
@HiltAndroidTest
class ChatsFragmentTest {

    private val timeMillis = 1618515166279L

    private fun chatsStub() = listOf(
        Chat("cr_1", "http://example.com", "John Doe", "+1335", "Hey", timeMillis),
        Chat("cr_2", "http://example.com", "Jane Doe", "+133785", "Hello", timeMillis)
    )

    @Inject
    lateinit var repository: FakeRepository

    @Inject
    lateinit var contentProvider: FakeContentProvider

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun emptyChatList_DisplaysNoChatsMessageAndHidesChatsList() {
        launchFragmentAndGetNavController()

        onView(withId(R.id.tvEmptyChats))
            .check(matches(isDisplayed()))
        onView(withId(R.id.chatsList))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun chatsNotEmpty_DisplaysChatsListAndHidesNoChatsMessage() {
        repository.setChats(chatsStub())
        launchFragmentAndGetNavController()

        onView(withId(R.id.chatsList))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyChats))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun chatListDisplaysChats() {
        val chats = chatsStub()
        repository.setChats(chats)
        launchFragmentAndGetNavController()

        for (chat in chats) {
            onView(withId(R.id.chatsList))
                .check(matches(hasDescendant(withText(chat.userName))))
                .check(matches(hasDescendant(withText(chat.recentMessage))))
                .check(matches(hasDescendant(withText(formattedTime(chat.timestamp)))))
        }
    }

    @Test
    fun clickingOnChatsItemOpensItsChat() {
        val chats = listOf(
            Chat("cr_1", "http://example.com", "John Doe", "+1335", "Hey"),
            Chat("cr_2", "http://example.com", "Jane Doe", "+133785", "Hello")
        )
        repository.setChats(chats)

        contentProvider.loggedIn = true
        // Register user
        val phoneNumber = "+13245558976"
        contentProvider.currentPhoneNumber = phoneNumber
        repository.saveUserDetailBlocking(UserDetail(phoneNumber, "John", "Doe"))

        val navController = launchFragmentAndGetNavController()

        // click chat list item
        onView(withId(R.id.chatsList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ChatListAdapter.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )

        // Verify navigation to chat
        assertThat(navController.currentDestination?.id, `is`(R.id.chatFragment))
    }

    private fun launchFragmentAndGetNavController(): NavController {
        val navController = getTestNavController(R.id.chatsFragment)
        contentProvider.navcontroller = navController
        launchFragmentInHiltContainer<ChatsFragment>(Bundle(), R.style.Theme_Telegram)
        return navController
    }

    private fun formattedTime(millis: Long): String {
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return time.format(Date(millis))
    }
}