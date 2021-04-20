package com.zancheema.android.telegram.auth

import android.os.Bundle
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.zancheema.android.telegram.R
import com.zancheema.android.telegram.di.AppContentModule
import com.zancheema.android.telegram.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@UninstallModules(AppContentModule::class)
@HiltAndroidTest
class AuthFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun changeCountryCode_CountryCodeAndNameAreReflected() {
        val countryCode = "92"
        val countryName = "Pakistan"

        launchFragmentInHiltContainer<AuthFragment>(Bundle(), R.style.Theme_Telegram)

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
    }
}