package com.smascaro.trackmixing.playbackservice.controller

import com.smascaro.trackmixing.common.models.TestModels
import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.playback.controller.TimestampUpdateThread
import com.smascaro.trackmixing.playback.utils.media.BandPlaybackHelper
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
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
    @Mock
    private lateinit var bandPlaybackHelper: BandPlaybackHelper
    private lateinit var eventBusTd: EventBusTd
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    // endregion helper fields
    @Before
    fun setup() {
        eventBusTd = EventBusTd()
        mockPlaybackHelperGetTrack()
        mockPlaybackHelperGetTimestampMillis()
        SUT =
            TimestampUpdateThread(bandPlaybackHelper, eventBusTd)
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
        SUT.cancel()
        // Assert
        //delay for 2 seconds as a coroutine does not get cancelled immediately and
        //an event could be sent between calling cancel() and actually stopping
        delay(2000)
        val eventsPostedAfter2secondsFromCancel = eventBusTd.eventsPostedCount
        delay(2000)
        val eventsPostedAfter4secondsFromCancel = eventBusTd.eventsPostedCount
        Assert.assertEquals(
            eventsPostedAfter2secondsFromCancel,
            eventsPostedAfter4secondsFromCancel
        )
    }
    // endregion tests
    // region helper methods
    private fun mockPlaybackHelperGetTrack() {
        `when`(bandPlaybackHelper.getTrack()).thenReturn(playingtrack)
    }

    private fun mockPlaybackHelperGetTimestampMillis() {
        `when`(bandPlaybackHelper.getTimestamp()).thenReturn(playingTrackTimestampMillis)
    }
    // endregion helper methods
    // region helper classes
    // endregion helper classes
}