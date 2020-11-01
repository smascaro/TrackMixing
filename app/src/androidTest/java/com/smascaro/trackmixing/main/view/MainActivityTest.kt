package com.smascaro.trackmixing.main.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.smascaro.trackmixing.R
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    @Before
    fun removeAnimations() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global TRANSITION_ANIMATION_SCALE 0")
            .checkError()
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global ANIMATOR_DURATION_SCALE 0")
            .checkError()
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global WINDOW_ANIMATION_SCALE 0")
            .checkError()
    }
    @After
    fun resetAnimations() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global TRANSITION_ANIMATION_SCALE 1")
            .checkError()
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global ANIMATOR_DURATION_SCALE 1")
            .checkError()
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global WINDOW_ANIMATION_SCALE 1")
            .checkError()
    }
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_MainActivity_runs_and_is_displayed() {
        onView(withId(R.id.motion_layout_main_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_TracksListFragment_is_displayed() {
        onView(withId(R.id.fragment_tracks_list_container)).check(matches(isDisplayed()))
    }

    @Test
    fun test_click_SearchButton_navigates_to_SearchSongFragment() {
        onView(withId(R.id.destination_search)).perform(click())
        onView(withId(R.id.fragment_song_search_container)).check(matches(isDisplayed()))
        pressBack()
        pressBack()
    }

    @Test
    fun test_click_SearchButton_navigates_to_SearchSongFragment_keyboard_is_visible() {
        onView(withId(R.id.destination_search)).perform(click())
        onView(withId(R.id.fragment_song_search_container)).check(matches(isDisplayed()))

        val keyboardManagerOnOpen = getKeyboardManager()
        Assert.assertTrue(keyboardManagerOnOpen.isAcceptingText)

        pressBack()
        pressBack()
    }

    private fun getKeyboardManager(): InputMethodManager {
        return InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}