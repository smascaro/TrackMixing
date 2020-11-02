package com.smascaro.trackmixing

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.AfterClass
import org.junit.BeforeClass

open class UiTest {
    companion object {
        @BeforeClass
        fun removeAnimations() {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global TRANSITION_ANIMATION_SCALE 0")
                .checkError()
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global ANIMATOR_DURATION_SCALE 0")
                .checkError()
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global WINDOW_ANIMATION_SCALE 0")
                .checkError()
        }
        @AfterClass
        fun resetAnimations() {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global TRANSITION_ANIMATION_SCALE 1")
                .checkError()
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global ANIMATOR_DURATION_SCALE 1")
                .checkError()
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb shell settings put global WINDOW_ANIMATION_SCALE 1")
                .checkError()
        }
    }
}