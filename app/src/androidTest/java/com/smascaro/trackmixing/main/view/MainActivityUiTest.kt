package com.smascaro.trackmixing.main.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class MainActivityUiTest : UiTest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivity_runs_and_is_displayed() {
        onView(withId(R.id.layout_main_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun tracksListFragment_is_displayed() {
        onView(withId(R.id.fragment_tracks_list_container)).check(matches(isDisplayed()))
    }
}