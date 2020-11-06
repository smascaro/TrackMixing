package com.smascaro.trackmixing.main.controller

import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainActivityControllerTest {
    private lateinit var SUT: MainActivityController

    // region constants

    // endregion constants

    // region helper fields
    @Mock lateinit var playbackStateManager: PlaybackStateManager

    @Mock lateinit var viewMvc: MainActivityViewMvc
    // endregion helper fields

    @Before
    fun setup() {
        SUT = MainActivityController(playbackStateManager)
        SUT.bindViewMvc(viewMvc)
    }

    // region tests
    @Test
    fun onStart_registersListener() {
        // Arrange
        // Act
        SUT.onStart()
        // Assert
        verify(viewMvc).registerListener(SUT)
    }

    @Test
    fun onStop_unregistersListener() {
        // Arrange
        // Act
        SUT.onStop()
        // Assert
        verify(viewMvc).unregisterListener(SUT)
    }

    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes
    // endregion helper classes
}