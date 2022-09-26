package com.smascaro.trackmixing.common.utils

import com.smascaro.trackmixing.base.time.TimeHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TimeHelperTest {
    private lateinit var SUT: TimeHelper

    // region constants

    // endregion constants

    // region helper fields

    // endregion helper fields

    @Before
    fun setup() {
        SUT = TimeHelper()
    }

    // region tests

    @Test
    fun test() {
        // Arrange

        // Act
        val seconds = TimeHelper.fromString("02:43").toSeconds()
        // Assert
        Assert.assertEquals(2 * 60 + 43, seconds)
    }
    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}