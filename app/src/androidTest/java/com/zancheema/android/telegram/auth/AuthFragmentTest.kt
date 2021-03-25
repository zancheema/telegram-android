package com.zancheema.android.telegram.auth

import android.os.Bundle
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.MainActivity
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.di.AppRepositoryModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppRepositoryModule::class)
@HiltAndroidTest
class AuthFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

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
    fun testPreconditions() {
        assertThat(Firebase.auth.currentUser, `is`(nullValue()))
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        launchFragmentInHiltContainer<AuthFragment>(Bundle(), R.style.Theme_Telegram) {
            Navigation.setViewNavController(view!!, navController)
        }
        assertThat(navController.currentDestination?.id, `is`(R.id.authFragment))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        // check auth fragment is shown
        onView(withId(R.id.authConstraintLayout))
            .check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun changeCountryCode_CountryCodeAndNameAreReflected() {
        val countryCode = "92"
        val countryName = "Pakistan"

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.countryCodePicker)).perform(ViewActions.click())
        onView(instanceOf(EditText::class.java))
            .inRoot(RootMatchers.isDialog())
            .perform(typeText("Pak"))
        onView(withText(containsString(countryName)))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())

        // Country code is displayed
        onView(withText(containsString(countryCode)))
            .check(matches(isDisplayed()))
        // Country name is displayed
        onView(withId(R.id.authCountry))
            .check(matches(withText(countryName)))

        activityScenario.close()
    }

    @Test
    fun signInUnregisteredPhoneNumber_OnCodeSent_NavigatesToVerifyCodeFragment() {
        val countryName = "United States"
        val phoneNumber = "7455551234"
        val settings = Firebase.auth.firebaseAuthSettings

        // Turn off phone auth app verification.
        settings.setAppVerificationDisabledForTesting(true)

        // Launch AuthFragment and set navController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        launchFragmentInHiltContainer<AuthFragment>(Bundle(), R.style.Theme_Telegram) {
            Navigation.setViewNavController(view!!, navController)
        }
        // Select Country
        onView(withId(R.id.countryCodePicker)).perform(ViewActions.click())
        onView(instanceOf(EditText::class.java))
            .inRoot(RootMatchers.isDialog())
            .perform(typeText("United S"))
        onView(withText(containsString(countryName)))
            .inRoot(RootMatchers.isDialog())
            .perform(ViewActions.click())
        // Enter Phone Number
        onView(withId(R.id.editPhoneNumber))
            .perform(typeText(phoneNumber), ViewActions.closeSoftKeyboard())
        // Click
        onView(withId(R.id.confirmPhoneNumberButton))
            .perform(ViewActions.click())

        // TODO: find some other way than sleep,
        //  Idling resource not working yet
        Thread.sleep(3000L)
        assertThat(navController.currentDestination?.id, `is`(R.id.verifyCodeFragment))

        // reset configurations
        settings.setAppVerificationDisabledForTesting(false)
    }

    @Test
    fun signInUnregisteredPhoneNumber_OnVerificationCompleted_NavigatesToRegisterFragment() {
        val countryName = "United States"
        val phoneNumber = "7455551234"
        val smsCode = "426800"
        val settings = Firebase.auth.firebaseAuthSettings

        // Turn off phone auth app verification.
        settings.setAppVerificationDisabledForTesting(true)
        // Configure faking the auto-retrieval with the whitelisted numbers.
        settings.setAutoRetrievedSmsCodeForPhoneNumber("+1$phoneNumber", smsCode)

        // Launch AuthFragment and set navController
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)
        launchFragmentInHiltContainer<AuthFragment>(Bundle(), R.style.Theme_Telegram) {
            Navigation.setViewNavController(view!!, navController)
        }
        // Select Country
        onView(withId(R.id.countryCodePicker)).perform(ViewActions.click())
        onView(instanceOf(EditText::class.java))
            .inRoot(RootMatchers.isDialog())
            .perform(typeText("United S"))
        onView(withText(containsString(countryName)))
            .inRoot(RootMatchers.isDialog())
            .perform(ViewActions.click())
        // Enter Phone Number
        onView(withId(R.id.editPhoneNumber))
            .perform(typeText(phoneNumber), ViewActions.closeSoftKeyboard())
        // Click
        onView(withId(R.id.confirmPhoneNumberButton))
            .perform(ViewActions.click())

        // TODO: find some other way than sleep,
        //  Idling resource not working yet
        Thread.sleep(3000L)
        assertThat(navController.currentDestination?.id, `is`(R.id.registerFragment))

        // reset configurations
        settings.setAppVerificationDisabledForTesting(false)
        settings.setAutoRetrievedSmsCodeForPhoneNumber("+10000000000", "000000")
    }
}