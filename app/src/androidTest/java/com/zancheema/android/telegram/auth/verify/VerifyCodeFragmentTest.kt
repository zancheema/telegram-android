package com.zancheema.android.telegram.auth.verify

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.data.source.FakeRepository
import com.zancheema.android.telegram.di.AppContentModule
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppContentModule::class)
@HiltAndroidTest
class VerifyCodeFragmentTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: FakeRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun enterSmsCodeTitleShowsPhoneNumberPassedInArgs() {
        val phoneNumber = "+17455551234"
        val args = VerifyCodeFragmentArgs(phoneNumber, "")
        launchFragmentInHiltContainer<VerifyCodeFragment>(args.toBundle(), R.style.Theme_Telegram)

        onView(withId(R.id.enterSmsCodeTitle))
            .check(matches(withText(containsString(phoneNumber))))
    }
}