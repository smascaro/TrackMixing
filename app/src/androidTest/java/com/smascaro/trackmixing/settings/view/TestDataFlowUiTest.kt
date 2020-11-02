package com.smascaro.trackmixing.settings.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.UiTest
import com.smascaro.trackmixing.settings.business.downloadtestdata.DownloadTestDataActivity
import org.junit.Rule
import org.junit.Test

class TestDataFlowUiTest : UiTest() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(DownloadTestDataActivity::class.java)

    @Test
    fun downloadTestDataActivity_runs_and_is_displayed() {
        onView(withId(R.id.fragment_select_test_data_container))
            .check(matches(isDisplayed()))
    }
}