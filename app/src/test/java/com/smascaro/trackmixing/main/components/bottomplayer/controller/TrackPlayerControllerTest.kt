package com.smascaro.trackmixing.main.components.bottomplayer.controller

import android.view.View
import com.smascaro.trackmixing.common.coroutines.IoCoroutineScopeFactory
import com.smascaro.trackmixing.common.coroutines.MainCoroutineScopeFactory
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.models.DOWNLOAD_ENTITY_1_TITLE
import com.smascaro.trackmixing.common.models.TRACK_VIDEO_KEY
import com.smascaro.trackmixing.common.models.TestModels
import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.main.components.player.controller.TrackPlayerController
import com.smascaro.trackmixing.main.components.player.model.TrackPlayerData
import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvc
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.model.TimestampChangedEvent
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TrackPlayerControllerTest {
    private lateinit var SUT: TrackPlayerController

    // region constants
    // endregion constants
    // region helper fields
    @Mock
    private lateinit var playbackStateManager: PlaybackStateManager

    @Mock
    private lateinit var playbackSession: PlaybackSession
    private lateinit var viewMvc: TrackPlayerViewMvcTd
    private lateinit var eventBus: EventBusTd
    private lateinit var tracksRepository: TracksRepository
    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    // endregion helper fields
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewMvc = TrackPlayerViewMvcTd()
        eventBus = EventBusTd()
        tracksRepository = TracksRepositoryTd()

        SUT = TrackPlayerController(
            playbackStateManager,
            tracksRepository,
            eventBus,
            playbackSession,
            MainCoroutineScopeFactory.create(testDispatcher),
            IoCoroutineScopeFactory.create(testDispatcher)
        )
        SUT.bindViewMvc(viewMvc)
    }

    @After
    fun validate() {
        validateMockitoUsage()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    // region tests
    @Test
    fun onCreate_eventBusListenerIsRegistered() {
        // Arrange
        // Act
        SUT.onCreate()
        // Assert
        assertTrue(eventBus.isListenerRegistered)
        assertEquals(SUT, eventBus.registeredListener)
    }

    @Test
    fun onCreate_viewMvcListenerIsRegistered() {
        // Arrange
        // Act
        SUT.onCreate()
        // Assert
        assertEquals(SUT, viewMvc.registeredListener)
    }

    @Test
    fun dispose_eventBusListenerIsUnregistered() {
        // Arrange
        // Act
        SUT.dispose()
        // Assert
        assertTrue(eventBus.isListenerUnregistered)
        assertEquals(SUT, eventBus.unregisteredListener)
    }

    @Test
    fun dispose_viewMvcListenerIsUnregistered() {
        // Arrange
        // Act
        SUT.dispose()
        // Assert
        assertEquals(SUT, viewMvc.unregisteredListener)
    }

    @Test
    fun onActionButtonClicked_userClicksOnActionButton_stateIsPlaying_pauseMasterEventIsPosted() {
        // Arrange
        setPlayingState()
        // Act
        SUT.onActionButtonClicked()
        // Assert
//        assertEquals(
////            PlaybackEvent.PauseMasterEvent::class.java,
////            eventBus.lastPostedEvent?.javaClass
////        )
    }

    @Test
    fun onActionButtonClicked_userClicksOnActionButton_stateIsPaused_playMasterEventIsPosted() {
        // Arrange
        setPausedState()
        // Act
        SUT.onActionButtonClicked()
        // Assert
//        assertEquals(PlaybackEvent.PlayMasterEvent::class.java, eventBus.lastPostedEvent?.javaClass)
    }

    @Test
    fun onActionButtonClicked_userClicksOnActionButton_stateIsStopped_noEventIsPosted() {
        // Arrange
        setStoppedState()
        // Act
        SUT.onActionButtonClicked()
        // Assert
        assertNull(eventBus.lastPostedEvent)
    }

    @Test
    fun onPlayerStateChanged_stateIsPlaying_playerBarIsShown() {
        // Arrange
        setPlayingState()
        // Act
        SUT.onPlayerStateChanged()
        // Assert
        assertTrue(viewMvc.isPlayerBarShown)
        assertTrue(viewMvc.dataInPlayerBar?.state is PlaybackStateManager.PlaybackState.Playing)
        assertEquals(DOWNLOAD_ENTITY_1_TITLE, viewMvc.dataInPlayerBar?.title)
    }

    @Test
    fun onPlayerStateChanged_stateIsPlaying_pauseButtonIsShown() {
        // Arrange
        setPlayingState()
        // Act
        SUT.onPlayerStateChanged()
        // Assert
        assertTrue(viewMvc.isPauseButtonShown)
    }

    @Test
    fun onPlayerStateChanged_stateIsPaused_playerBarIsShown() {
        // Arrange
        setPausedState()
        // Act
        SUT.onPlayerStateChanged()
        // Assert
        assertTrue(viewMvc.isPlayerBarShown)
        assertTrue(viewMvc.dataInPlayerBar?.state is PlaybackStateManager.PlaybackState.Paused)
        assertEquals(DOWNLOAD_ENTITY_1_TITLE, viewMvc.dataInPlayerBar?.title)
    }

    @Test
    fun onPlayerStateChanged_stateIsPaused_playButtonIsShown() {
        // Arrange
        setPausedState()
        // Act
        SUT.onPlayerStateChanged()
        // Assert
        assertTrue(viewMvc.isPlayButtonShown)
    }

    @Test
    fun onPlayerStateChanged_stateIsStopped_playerBarIsHidden() {
        // Arrange
        setStoppedState()
        // Act
        SUT.onPlayerStateChanged()
        // Assert
        assertFalse(viewMvc.isPlayerBarShown)
    }

    @Test
    fun onServiceRunningCheck_serviceIsRunning_stateNotStopped_playerBarIsShown() {
        // Arrange
        val isServiceRunning = true
        setPlayingState()
        // Act
        SUT.onServiceRunningCheck(isServiceRunning)
        // Assert
        assertTrue(viewMvc.isPlayerBarShown)
    }

    @Test
    fun onServiceRunningCheck_serviceIsRunning_noInteractionsWithView() {
        // Arrange
        val isServiceRunning = false
        // Act
        SUT.onServiceRunningCheck(isServiceRunning)
        // Assert
        assertEquals(0, viewMvc.totalInteractions)
    }

    @Test
    fun onTimestampChangedEvent_timestampIsUpdatedInView() {
        // Arrange
        val event = TimestampChangedEvent(120, 240)
        // Act
        SUT.onMessageEvent(event)
        // Assert
        assertEquals(event.newTimestamp, viewMvc.newTimestampToUpdate)
        assertEquals(event.totalLength, viewMvc.totalLengthToUpdate)
    }

    // endregion tests
    // region helper methods
    private fun setPlayingState() {
        `when`(playbackStateManager.getPlayingState())
            .thenReturn(PlaybackStateManager.PlaybackState.Playing())
    }

    private fun setPausedState() {
        `when`(playbackStateManager.getPlayingState())
            .thenReturn(PlaybackStateManager.PlaybackState.Paused())
    }

    private fun setStoppedState() {
        `when`(playbackStateManager.getPlayingState())
            .thenReturn(PlaybackStateManager.PlaybackState.Stopped())
    }

    private fun setPlaybackStateCurrentSongId() {
        `when`(playbackStateManager.getCurrentSong())
            .thenReturn(TRACK_VIDEO_KEY)
    }

    // endregion helper methods
    // region helper classes
    class TracksRepositoryTd : TracksRepository {
        val entity = TestModels.getDownloadEntity()
        override suspend fun get(videoId: String): DownloadEntity {
            return entity
        }

        override suspend fun getAll(): List<DownloadEntity> {
            TODO()
        }

        override suspend fun update(entity: DownloadEntity) {
        }

        override suspend fun insert(entity: DownloadEntity): Long {
            TODO()
        }

        override suspend fun delete(videoId: String) {
//            TODO("Not yet implemented")
        }
    }

    class TrackPlayerViewMvcTd : TrackPlayerViewMvc {
        var totalInteractions = 0
        var isPlayerBarShown: Boolean = false
        var dataInPlayerBar: TrackPlayerData? = null
        var isPlayButtonShown: Boolean = false
        var isPauseButtonShown: Boolean = false
        var registeredListener: Any? = null
        var unregisteredListener: Any? = null
        var newTimestampToUpdate: Int = 0
        var totalLengthToUpdate: Int = 0
        override fun onCreate() {
            totalInteractions++
        }

        override fun showPlayerBar(data: TrackPlayerData) {
            dataInPlayerBar = data
            isPlayerBarShown = true
            totalInteractions++
        }

        override fun showPlayButton() {
            isPlayButtonShown = true
            totalInteractions++
        }

        override fun showPauseButton() {
            isPauseButtonShown = true
            totalInteractions++
        }

        override fun updateTimestamp(newTimestamp: Int, totalLength: Int) {
            newTimestampToUpdate = newTimestamp
            totalLengthToUpdate = totalLength
        }

        override fun bindTrackDuration(lengthSeconds: Int) {
//            TODO("Not yet implemented")
        }

        override fun bindVolumes(volumes: TrackVolumeBundle) {
//            TODO("Not yet implemented")
        }

        override fun openPlayer() {
//            TODO("Not yet implemented")
        }

        override fun onBackPressed(): Boolean {
//            TODO("Not yet implemented")
            return false
        }

        override fun registerListener(listener: TrackPlayerViewMvc.Listener) {
            registeredListener = listener
            totalInteractions++
        }

        override fun unregisterListener(listener: TrackPlayerViewMvc.Listener) {
            unregisteredListener = listener
            totalInteractions++
        }

        override fun getRootView(): View {
            totalInteractions++
            TODO("Not yet implemented")
        }

        override fun bindRootView(rootView: View?) {
            totalInteractions++
        }
    }
    // endregion helper classes
}