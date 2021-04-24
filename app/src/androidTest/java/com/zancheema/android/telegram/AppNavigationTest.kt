package com.zancheema.android.telegram

import android.view.Gravity
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.zancheema.android.telegram.data.source.FakeContentProvider
import com.zancheema.android.telegram.data.source.FakeRepository
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppContentModule
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
@LargeTest
@UninstallModules(AppContentModule::class)
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
        val activityScenario = launchActivity()

        onView(withId(R.id.authConstraintLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun userLoggedInButNotRegistered_DisplaysRegistration() {
        contentProvider.loggedIn = true
        val activityScenario = launchActivity()

        onView(withId(R.id.registerLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun userLoggedInAndRegistered_DisplaysChats() {
        contentProvider.loggedIn = true
        registerUser()
        val activityScenario = launchActivity()

        onView(withId(R.id.chatsLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun chatsScreen_ClickOnDrawerIcon_OpensDrawer() {
        contentProvider.loggedIn = true
        registerUser()
        val activityScenario = launchActivity()

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
        val activityScenario = launchActivity()

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

    private fun launchActivity(): ActivityScenario<MainActivity> {
        // Temporary set the navController to test navController
        // to escape the exception
        contentProvider.navcontroller = getTestNavController()

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            // once activity is launched set the navController
            // to real navController
            // because real navController is needed to get contentDescription

            contentProvider.navcontroller =
                Navigation.findNavController(activity, R.id.navHostFragment)
        }

        return activityScenario
    }

    private fun registerUser() {
        val phoneNumber = "+13245558976"
        contentProvider.currentPhoneNumber = phoneNumber
        repository.saveUserDetailBlocking(UserDetail(phoneNumber, "John", "Doe"))
    }
}