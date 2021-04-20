package com.zancheema.android.telegram.register

import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.succeeded
import com.zancheema.android.telegram.di.AppContentModule
import com.zancheema.android.telegram.getTestNavController
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppContentModule::class)
@HiltAndroidTest
class RegisterFragmentTest {

    private val phoneNumber = "+17455551234"

    @Inject
    lateinit var repository: FakeRepository

    @Inject
    lateinit var contentProvider: FakeContentProvider

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() = runBlocking {
        hiltRule.inject()
        repository.saveUser(User(phoneNumber))
    }

    @Test
    fun launchSuccessfully() {
        val args = RegisterFragmentArgs(phoneNumber)
        launchFragmentAndGetNavController(args)

        onView(withId(R.id.registerLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun clickFabNext_ValidDetail_SavesUserDetailInRepositoryAndNavigatesToChats() =
        runBlocking {
            val args = RegisterFragmentArgs(phoneNumber)

            val navController = launchFragmentAndGetNavController(args)

            val firstName = "John"
            val lastName = "Doe"

            onView(withId(R.id.etFirstName))
                .perform(typeText(firstName), closeSoftKeyboard())
            onView(withId(R.id.etLastName))
                .perform(typeText(lastName), closeSoftKeyboard())
            onView(withId(R.id.fabNext))
                .perform(click())

            // user detail is saved in repository
            val userDetail = repository.getUserDetail(phoneNumber)
            assertThat(userDetail.succeeded, `is`(true))
            userDetail as Success
            assertThat(userDetail.data.phoneNumber, `is`(phoneNumber))
            assertThat(userDetail.data.firstName, `is`(firstName))
            assertThat(userDetail.data.lastName, `is`(lastName))
            // chats fragment is displayed
            assertThat(navController.currentDestination?.id, `is`(R.id.chatsFragment))
        }

    private fun launchFragmentAndGetNavController(args: RegisterFragmentArgs): NavController {
        val navController = getTestNavController(R.id.registerFragment)
        contentProvider.navcontroller = navController
        launchFragmentInHiltContainer<RegisterFragment>(args.toBundle(), R.style.Theme_Telegram)
        return navController
    }
}