package com.zancheema.android.telegram.contacts

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.GrantPermissionRule
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.AppContentProvider
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import com.zancheema.android.telegram.di.AppRepositoryModule
import com.zancheema.android.telegram.di.AppContentProviderModule
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import com.zancheema.android.telegram.source.FakeRepository
import com.zancheema.android.telegram.source.TestContentProvider
import com.zancheema.android.telegram.util.saveUserBlocking
import com.zancheema.android.telegram.util.saveUserDetailBlocking
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppRepositoryModule::class, AppContentProviderModule::class)
@HiltAndroidTest
class ContactsFragmentTest {

    @Inject
    lateinit var repository: FakeRepository

    @Inject
    lateinit var contentProvider: TestContentProvider

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
        launchFragmentInHiltContainer<ContactsFragment>(Bundle(), R.style.Theme_Telegram)
        onView(withId(R.id.contactList))
            .check(matches(isDisplayed()))
    }

    @Test
    fun contactsListDisplaysUserDetails() {
        contentProvider.phoneNumbers = listOf(user1.phoneNumber, user2.phoneNumber)
        launchFragmentInHiltContainer<ContactsFragment>(Bundle(), R.style.Theme_Telegram)

        onView(withId(R.id.contactList)).apply {
            check(matches(hasDescendant(withText(userDetail1.fullName))))
            check(matches(hasDescendant(withText(userDetail2.fullName))))
            check(matches(not(hasDescendant(withText(userDetail3.fullName)))))
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class TestContentProviderModule {
        @Singleton
        @Binds
        abstract fun provideTestContentProvider(provider: TestContentProvider): AppContentProvider
    }
}