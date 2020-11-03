package com.smascaro.trackmixing.search.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.UiTest
import com.smascaro.trackmixing.main.view.MainActivity
import org.hamcrest.Matchers
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SongSearchUiTest : UiTest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun click_SearchButton_navigates_to_SearchSongFragment() {
        navigateToSearchFragment()
        onView(withId(R.id.fragment_song_search_container))
            .check(matches(isDisplayed()))
        Espresso.pressBack()
        Espresso.pressBack()
    }

    @Test
    fun click_SearchButton_navigates_to_SearchSongFragment_keyboard_is_visible() {
        navigateToSearchFragment()
        onView(withId(R.id.fragment_song_search_container))
            .check(matches(isDisplayed()))

        val keyboardManagerOnOpen = getKeyboardManager()
        assertTrue(keyboardManagerOnOpen.isAcceptingText)

        Espresso.pressBack()
        Espresso.pressBack()
    }

    @Test
    fun input_search_text_in_search_fragment_works_correctly() {
        navigateToSearchFragment()
        val textToSearch = "ramones"
        onView(withId(R.id.et_toolbar_search_input))
            .perform(typeText(textToSearch))
        onView(ViewMatchers.withText(textToSearch))
            .check(matches(Matchers.instanceOf(EditText::class.java)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun input_search_text_in_search_fragment_clear_button_deletes_text() {
        navigateToSearchFragment()
        val textToSearch = "ramones"
        onView(withId(R.id.et_toolbar_search_input))
            .perform(typeText(textToSearch))
        onView(withId(R.id.iv_toolbar_search_clear)).perform(ViewActions.click())
        try {
            onView(ViewMatchers.withText(textToSearch))
                .check(matches(isDisplayed()))
            assertTrue(false)
        } catch (nmve: NoMatchingViewException) {
            assertTrue(true)
        }
        onView(withId(R.id.et_toolbar_search_input))
            .check(matches(ViewMatchers.withText("")))
    }

    @Test
    fun input_search_text_in_search_fragment_search_button_triggers_progress_bar() {
        navigateToSearchFragment()
        val textToSearch = "ramones"
        onView(withId(R.id.et_toolbar_search_input))
            .perform(typeText(textToSearch))
        onView(withId(R.id.et_toolbar_search_input)).perform(pressImeActionButton())
        onView(withId(R.id.pb_search_loading_progress)).check(matches(isDisplayed()))
    }

    private fun navigateToSearchFragment() {
        onView(withId(R.id.destination_search)).perform(ViewActions.click())
    }

    private fun getKeyboardManager(): InputMethodManager {
        return InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}