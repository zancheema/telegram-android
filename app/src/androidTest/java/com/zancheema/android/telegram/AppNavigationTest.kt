package com.zancheema.android.telegram

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppContentProviderModule
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.source.FakeContentProvider
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
        val phoneNumber = "+13245558976"
        contentProvider.loggedIn = true
        contentProvider.phoneNumber = phoneNumber
        repository.saveUserDetailBlocking(UserDetail(phoneNumber, "John", "Doe"))
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.chatsLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class TestContentProviderModule {
        @Singleton
        @Binds
        abstract fun provideTestContentProvider(provider: FakeContentProvider): AppContentProvider
    }
}