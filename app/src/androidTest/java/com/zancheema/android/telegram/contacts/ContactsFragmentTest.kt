package com.zancheema.android.telegram.contacts

import android.os.Bundle
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.chats.ChatListAdapter
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppContentModule
import com.zancheema.android.telegram.getTestNavController
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.util.saveUserBlocking
import com.zancheema.android.telegram.util.saveUserDetailBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppContentModule::class)
@HiltAndroidTest
class ContactsFragmentTest {

    @Inject
    lateinit var repository: FakeRepository

    @Inject
    lateinit var contentProvider: FakeContentProvider

    private val user1 = User("+12345556543")
    private val user2 = User("+312345556703")
    private val user3 = User("+12095556123")
    private val userDetail1 = UserDetail(user1.phoneNumber, "John", "Doe")
    private val userDetail2 = UserDetail(user2.phoneNumber, "Jane", "Doe")
    private val userDetail3 = UserDetail(user3.phoneNumber, "Mike", "Doe")

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS)

    @Before
    fun init() {
        hiltRule.inject()

        repository.apply {
            saveUserBlocking(user1)
            saveUserBlocking(user2)
            saveUserBlocking(user3)
            saveUserDetailBlocking(userDetail1)
            saveUserDetailBlocking(userDetail2)
            saveUserDetailBlocking(userDetail3)
        }
    }

    @Test
    fun launchesSuccessfully() {
        launchFragmentAndGetNavController()

        onView(withId(R.id.contactList))
            .check(matches(isDisplayed()))
    }

    @Test
    fun contactsListDisplaysUserDetails() {
        contentProvider.currentPhoneNumber = user1.phoneNumber
        contentProvider.contactPhoneNumbers = listOf(user2.phoneNumber, user3.phoneNumber)
        launchFragmentAndGetNavController()

        onView(withId(R.id.contactList)).apply {
            check(matches(hasDescendant(withText(userDetail2.fullName))))
            check(matches(hasDescendant(withText(userDetail3.fullName))))
            check(matches(not(hasDescendant(withText(userDetail1.fullName)))))
        }
    }

    @Test
    fun clickingContactsItemOpensChat() {
        // Set Preconditions
        contentProvider.currentPhoneNumber = user1.phoneNumber
        contentProvider.contactPhoneNumbers = listOf(user2.phoneNumber, user3.phoneNumber)
        // Launch contacts
        val navController = launchFragmentAndGetNavController()

        // click contact list item
        onView(withId(R.id.contactList))
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
        val navController = getTestNavController(R.id.contactsFragment)
        contentProvider.navcontroller = navController
        launchFragmentInHiltContainer<ContactsFragment>(Bundle(), R.style.Theme_Telegram)
        return navController
    }
}