package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.models.TestModels
import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.utils.PlaybackHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.junit.MockitoJUnitRunner

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TimestampUpdateThreadTest {
    private lateinit var SUT: TimestampUpdateThread

    // region constants
    private val playingtrack = TestModels.getTrack()
    private val playingTrackTimestampMillis = 100 * 1000
    private val playingTrackTimestampSeconds = 100
    // endregion constants

    // region helper fields
    @Mock private lateinit var playbackHelper: PlaybackHelper
    private lateinit var eventBusTd: EventBusTd
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    // endregion helper fields

    @Before
    fun setup() {
        eventBusTd = EventBusTd()
        mockPlaybackHelperGetTrack()
        mockPlaybackHelperGetTimestampMillis()
        SUT = TimestampUpdateThread(playbackHelper, eventBusTd)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        validateMockitoUsage()
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }


    // region tests
    @Test
    fun start_threadIsRunningAndTimestampEventIsPosted() = runBlocking {
        // Arrange
        // Act
        SUT.start()
        // Assert
        delay(500)
        val postedEvent = eventBusTd.lastPostedEvent as PlaybackEvent.TimestampChanged
        Assert.assertEquals(playingTrackTimestampSeconds, postedEvent.newTimestamp)
        Assert.assertEquals(playingtrack.secondsLong, postedEvent.totalLength)
    }

    @Test
    fun cancel_threadIsCancelledAndNoMoreEventsArePosted() = runBlocking {
        // Arrange
        // Act
        SUT.start()
        delay(2000)
        val eventsPostedBeforeCancel = eventBusTd.eventsPostedCount
        SUT.cancel()
        delay(2000)
        // Assert
        val eventsPostedAfterCancel = eventBusTd.eventsPostedCount
        Assert.assertEquals(eventsPostedBeforeCancel, eventsPostedAfterCancel)
    }
    // endregion tests

    // region helper methods
    fun mockPlaybackHelperGetTrack() {
        `when`(playbackHelper.getTrack()).thenReturn(playingtrack)
    }

    fun mockPlaybackHelperGetTimestampMillis() {
        `when`(playbackHelper.getTimestampMillis()).thenReturn(playingTrackTimestampMillis)
    }
    // endregion helper methods

    // region helper classes

    // endregion helper classes
}