package com.zancheema.android.telegram

import android.view.Gravity
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppContentModule
import com.zancheema.android.telegram.source.FakeContentProvider
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.util.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(AppContentModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: FakeRepository

    @Inject
    lateinit var contentProvider: FakeContentProvider

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

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
    fun authenticateUserWithValidUnregisteredCredentials_DisplaysRegisterFragment() =
        runBlocking {
            val countryName = "United States"
            val phoneNumber = "7455551234"
            val smsCode = "426800"
            val settings = Firebase.auth.firebaseAuthSettings
            // Turn off phone auth app verification.
            settings.setAppVerificationDisabledForTesting(true)
            val activityScenario = launchActivity()
            dataBindingIdlingResource.monitorActivity(activityScenario)

            // set country code
            onView(withId(R.id.countryCodePicker)).perform(click())
            onView(CoreMatchers.instanceOf(EditText::class.java))
                .inRoot(RootMatchers.isDialog())
                .perform(typeText("United S"))
            onView(withText(CoreMatchers.containsString(countryName)))
                .inRoot(RootMatchers.isDialog())
                .perform(click())
            // Enter Phone Number
            onView(withId(R.id.editPhoneNumber))
                .perform(typeText(phoneNumber), closeSoftKeyboard())
            // confirm phone number (click NEXT)
            onView(withId(R.id.confirmPhoneNumberButton))
                .perform(click())

            // check VerifyCodeFragment is displayed
            onView(withId(R.id.verifyCodeFragmentLayout))
                .check(matches(isDisplayed()))

            // enter sms code
            onView(withId(R.id.editSmsCode))
                .perform(typeText(smsCode), closeSoftKeyboard())
            // confirm code (click NEXT)
            onView(withId(R.id.confirmSmsCodeButton))
                .perform(click())

            // check RegisterFragment is displayed (user not registered already)
            onView(withId(R.id.registerLayout))
                .check(matches(isDisplayed()))

            // close activity
            activityScenario.close()
        }

    @Test
    fun authenticateUserWithAlreadyRegisteredCredentials_DisplaysChatsFragment() {
        val countryName = "United States"
        val phoneNumber = "7455551234"
        val smsCode = "426800"
        val settings = Firebase.auth.firebaseAuthSettings
        // already register the user
        val user = User("+1$phoneNumber")
        val userDetail = UserDetail(user.phoneNumber, "John", "Doe")
        repository.saveUserBlocking(user)
        repository.saveUserDetailBlocking(userDetail)
        // Turn off phone auth app verification.
        settings.setAppVerificationDisabledForTesting(true)
        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // set country code
        onView(withId(R.id.countryCodePicker)).perform(click())
        onView(CoreMatchers.instanceOf(EditText::class.java))
            .inRoot(RootMatchers.isDialog())
            .perform(typeText("United S"))
        onView(withText(CoreMatchers.containsString(countryName)))
            .inRoot(RootMatchers.isDialog())
            .perform(click())
        // Enter Phone Number
        onView(withId(R.id.editPhoneNumber))
            .perform(typeText(phoneNumber), closeSoftKeyboard())
        // confirm phone number (click NEXT)
        onView(withId(R.id.confirmPhoneNumberButton))
            .perform(click())

        // check VerifyCodeFragment is displayed
        onView(withId(R.id.verifyCodeFragmentLayout))
            .check(matches(isDisplayed()))

        // enter sms code
        onView(withId(R.id.editSmsCode))
            .perform(typeText(smsCode), closeSoftKeyboard())
        // confirm code (click NEXT)
        onView(withId(R.id.confirmSmsCodeButton))
            .perform(click())

        // check ChatsFragment is displayed (user already registered)
        onView(withId(R.id.chatsLayout))
            .check(matches(isDisplayed()))

        // close activity
        activityScenario.close()
    }

    @Test
    fun userLoggedInAndRegister_DrawerHeaderDisplaysUserDetail() {
        contentProvider.loggedIn = true
        val phoneNumber = "+13245558976"
        contentProvider.currentPhoneNumber = phoneNumber
        val userDetail = UserDetail(phoneNumber, "John", "Doe")
        repository.saveUserDetailBlocking(userDetail)
        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Drawer is closed by default
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(DrawerMatchers.isClosed(Gravity.START)))

        // Open drawer
        onView(
            withContentDescription(
                activityScenario.getToolbarNavigationContentDescription()
            )
        ).perform(click())

        // Check if the drawer is open
        onView(withId(R.id.mainDrawerLayout))
            .check(matches(DrawerMatchers.isOpen(Gravity.START)))
        // Check if the drawer header displays user details
        onView(withId(R.id.tvUsername))
            .check(matches(withText(userDetail.fullName)))
        onView(withId(R.id.tvPhoneNumber))
            .check(matches(withText(userDetail.phoneNumber)))

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
}