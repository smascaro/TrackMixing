package com.smascaro.trackmixing.settingsOld.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.UiTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SettingsActivityUiTest : UiTest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(com.smascaro.trackmixing.settings.view.SettingsActivity::class.java)

    @Test
    fun test_SettingsActivity_runs_and_is_displayed() {
        onView(withId(R.id.activity_settings_container)).check(matches(isDisplayed()))
    }

    @Test
    fun test_SettingsActivity_contains_test_data_preference_visible() {
        onView(ViewMatchers.withText("Download test data")).perform(ViewActions.click())
        onView(withId(R.id.fragment_select_test_data_container))
            .check(matches(isDisplayed()))
    }
}