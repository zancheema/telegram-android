package com.zancheema.android.telegram

import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.source.saveUserBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(AppRepositoryModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: FakeRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @After
    fun signOut() {
        Firebase.auth.apply {
            if (currentUser != null) signOut()
        }
    }

    @Test
    fun testPreConditions() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        // check auth fragment is shown
        onView(withId(R.id.authConstraintLayout))
            .check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun authenticateUserWithValidUnregisteredCredentials_NavigatesToRegisterFragment() {
        val countryName = "United States"
        val phoneNumber = "7455551234"
        val smsCode = "426800"
        val settings = Firebase.auth.firebaseAuthSettings
        // Turn off phone auth app verification.
        settings.setAppVerificationDisabledForTesting(true)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // set country code
        onView(withId(R.id.countryCodePicker)).perform(click())
        onView(CoreMatchers.instanceOf(EditText::class.java))
            .inRoot(RootMatchers.isDialog())
            .perform(typeText("United S"))
        onView(ViewMatchers.withText(CoreMatchers.containsString(countryName)))
            .inRoot(RootMatchers.isDialog())
            .perform(click())
        // Enter Phone Number
        onView(withId(R.id.editPhoneNumber))
            .perform(typeText(phoneNumber), closeSoftKeyboard())
        // confirm phone number (click NEXT)
        onView(withId(R.id.confirmPhoneNumberButton))
            .perform(click())

        // TODO: find some other way than sleep,
        //  Idling resource not working yet
        Thread.sleep(3000L)
        // check VerifyCodeFragment is displayed
        onView(withId(R.id.verifyCodeFragmentLayout))
            .check(matches(isDisplayed()))

        // enter sms code
        onView(withId(R.id.editSmsCode))
            .perform(typeText(smsCode), closeSoftKeyboard())
        // confirm code (click NEXT)
        onView(withId(R.id.confirmSmsCodeButton))
            .perform(click())

        Thread.sleep(3000L)
        // check RegisterFragment is displayed (user not registered already)
        onView(withId(R.id.registerFragmentLayout))
            .check(matches(isDisplayed()))

        // close activity
        activityScenario.close()
    }

    @Test
    fun authenticateUserWithAlreadyRegisteredCredentials_ShowsChatsFragment() {
        val countryName = "United States"
        val phoneNumber = "7455551234"
        val smsCode = "426800"
        val settings = Firebase.auth.firebaseAuthSettings
        // already register the user
        repository.saveUserBlocking(User("+1$phoneNumber"))
        // Turn off phone auth app verification.
        settings.setAppVerificationDisabledForTesting(true)
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // set country code
        onView(withId(R.id.countryCodePicker)).perform(click())
        onView(CoreMatchers.instanceOf(EditText::class.java))
            .inRoot(RootMatchers.isDialog())
            .perform(typeText("United S"))
        onView(ViewMatchers.withText(CoreMatchers.containsString(countryName)))
            .inRoot(RootMatchers.isDialog())
            .perform(click())
        // Enter Phone Number
        onView(withId(R.id.editPhoneNumber))
            .perform(typeText(phoneNumber), closeSoftKeyboard())
        // confirm phone number (click NEXT)
        onView(withId(R.id.confirmPhoneNumberButton))
            .perform(click())

        // TODO: find some other way than sleep,
        //  Idling resource not working yet
        Thread.sleep(3000L)
        // check VerifyCodeFragment is displayed
        onView(withId(R.id.verifyCodeFragmentLayout))
            .check(matches(isDisplayed()))

        // enter sms code
        onView(withId(R.id.editSmsCode))
            .perform(typeText(smsCode), closeSoftKeyboard())
        // confirm code (click NEXT)
        onView(withId(R.id.confirmSmsCodeButton))
            .perform(click())

        Thread.sleep(3000L)
        // check ChatsFragment is displayed (user already registered)
        onView(withId(R.id.chatsFragmentLayout))
            .check(matches(isDisplayed()))

        // close activity
        activityScenario.close()
    }
}