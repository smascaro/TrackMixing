package com.smascaro.trackmixing.settings.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.smascaro.trackmixing.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SettingsActivityTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Test
    fun test_SettingsActivity_runs_and_is_displayed() {
        onView(withId(R.id.activity_settings_container)).check(matches(isDisplayed()))
    }

    @Test
    fun test_SettingsActivity_contains_test_data_preference_visible() {
        onView(withText("Download test data")).perform(click())
        onView(withId(R.id.fragment_select_test_data_container)).check(matches(isDisplayed()))
    }
}