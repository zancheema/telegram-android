package com.zancheema.android.telegram

import android.view.Gravity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.zancheema.android.telegram.chats.ChatListAdapter
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.domain.Chat
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppContentProviderModule
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.util.saveUserDetailBlocking
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(AppRepositoryModule::class, AppContentProviderModule::class)
@HiltAndroidTest
class AppNavigationTest {

    @Inject
    lateinit var repository: FakeRepository

    @Inject
    lateinit var contentProvider: FakeContentProvider

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userLoggedOut_DisplaysAuth() {
        contentProvider.loggedIn = false
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.authConstraintLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun userLoggedInButNotRegistered_DisplaysRegistration() {
        contentProvider.loggedIn = true
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.registerLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun userLoggedInAndRegistered_DisplaysChats() {
        contentProvider.loggedIn = true
        registerUser()
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.chatsLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun chatsScreen_ClickOnDrawerIcon_OpensDrawer() {
        contentProvider.loggedIn = true
        registerUser()
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Drawer is closed by default
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(isClosed(Gravity.START)))

        // Open drawer
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check if the drawer is open
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(isOpen(Gravity.START)))
        // When using ActivityScenario.launch, always call close()
        activityScenario.close()
    }

    @Test
    fun drawerNavigationFromChatsToContactsAndNavigateUpToChats() {
        contentProvider.loggedIn = true
        registerUser()
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Drawer is closed by default
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(isClosed(Gravity.START)))

        // Open drawer
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(isClosed(Gravity.START)))
            .perform(open())

        // Start contacts screen
        onView(withId(R.id.navView))
            .perform(navigateTo(R.id.contactsFragment))

        // Check that contacts screen is displayed
        onView(withId(R.id.contactsLayout)).check(matches(isDisplayed()))

        // Click home icon (navigate up)
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that contacts screen is displayed
        onView(withId(R.id.chatsLayout)).check(matches(isDisplayed()))

        // When using ActivityScenario.launch, always call close()
        activityScenario.close()
    }

    @Test
    fun clickingOnChatsItemOpensItsChat() {
        val chats = listOf(
            Chat("cr_1", "http://example.com", "John Doe", "+1335", "Hey"),
            Chat("cr_2", "http://example.com", "Jane Doe", "+133785", "Hello")
        )
        repository.setChats(chats)

        contentProvider.loggedIn = true
        registerUser()
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        val position = 0
        val selectedChat = chats[position]
        // click chat list item
        onView(withId(R.id.chatsList))
            .perform(actionOnItemAtPosition<ChatListAdapter.ViewHolder>(position, click()))

        // Chat screen is displayed
        onView(withId(R.id.chatLayout))
            .check(matches(isDisplayed()))
        // Toolbar displays selected chat's data
        onView(withId(R.id.tvTitle))
            .check(matches(withText(selectedChat.userName)))

        // Click home icon (navigate up)
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check that contacts screen is displayed
        onView(withId(R.id.chatsLayout)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    private fun registerUser() {
        val phoneNumber = "+13245558976"
        contentProvider.phoneNumber = phoneNumber
        repository.saveUserDetailBlocking(UserDetail(phoneNumber, "John", "Doe"))
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class TestContentProviderModule {
        @Singleton
        @Binds
        abstract fun provideTestContentProvider(provider: FakeContentProvider): AppContentProvider
    }
}