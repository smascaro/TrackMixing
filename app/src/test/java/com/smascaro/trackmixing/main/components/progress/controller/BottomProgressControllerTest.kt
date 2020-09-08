package com.smascaro.trackmixing.main.components.progress.controller

import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BottomProgressControllerTest {
    private lateinit var SUT: BottomProgressController

    // region constants

    // endregion constants

    // region helper fields
    @Mock private lateinit var resourcesWrapper: ResourcesWrapper
    @Mock private lateinit var viewMvc: BottomProgressViewMvc
    private lateinit var eventBus: EventBusTd
    // endregion helper fields

    @Before
    fun setup() {
        eventBus = EventBusTd()
        SUT = BottomProgressController(resourcesWrapper, eventBus)
        SUT.bindViewMvc(viewMvc)
    }

    @Test
    fun onCreate_startMarqueeEffect() {
        // Arrange
        // Act
        SUT.onCreate()
        // Assert
        verify(viewMvc).startMarquee()
    }

    @Test
    fun onStart_eventBusRegistered() {
        // Arrange
        // Act
        SUT.onStart()
        // Assert
        assertTrue(eventBus.isListenerRegistered)
        assertEquals(SUT, eventBus.registeredListener)
    }

    @Test
    fun onStop_eventBusUnregistered() {
        // Arrange
        // Act
        SUT.onStop()
        // Assert
        assertTrue(eventBus.isListenerUnregistered)
        assertEquals(SUT, eventBus.unregisteredListener)
    }
    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}