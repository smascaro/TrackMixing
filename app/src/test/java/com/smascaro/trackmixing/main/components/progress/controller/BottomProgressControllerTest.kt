package com.smascaro.trackmixing.main.components.progress.controller

import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.base.events.UiProgressEvent
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BottomProgressControllerTest {
    private lateinit var SUT: BottomProgressController

    // region constants
    // endregion constants

    // region helper fields
    @Mock private lateinit var resourcesWrapper: ResourcesWrapper
    @Mock private lateinit var viewMvc: BottomProgressViewMvc
    private lateinit var eventBus: EventBusTd
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    // endregion helper fields

    @Before
    fun setup() {
        eventBus = EventBusTd()
        SUT = BottomProgressController(resourcesWrapper, eventBus)
        SUT.bindViewMvc(viewMvc)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        validateMockitoUsage()
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
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

    @Test
    fun onProgressUpdateEvent_updatesView() = runBlocking {
        // Arrange
        val event = UiProgressEvent.ProgressUpdate(50, "Test status")
        // Act
        SUT.onMessageEvent(event)
        // Assert
        delay(500)
        verify(viewMvc).showProgressBar()
        verify(viewMvc).updateProgress(event.progress, event.status)
    }

    @Test
    fun onErrorOccurredEvent_updatesView() = runBlocking {
        // Arrange
        val event = UiProgressEvent.ErrorOccurred("Some test error message")
        // Act
        SUT.onMessageEvent(event)
        // Assert
        delay(500)
        verify(viewMvc).hideProgressBar()
    }

    @Test
    fun onProgressFinishedEvent_updatesView() = runBlocking {
        // Arrange
        val event = UiProgressEvent.ProgressFinished()
        // Act
        SUT.onMessageEvent(event)
        // Assert
        delay(500)
        verify(viewMvc).hideProgressBar()
    }
    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}