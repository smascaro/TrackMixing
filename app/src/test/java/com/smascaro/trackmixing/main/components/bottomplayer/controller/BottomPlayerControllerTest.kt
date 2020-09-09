package com.smascaro.trackmixing.main.components.bottomplayer.controller

import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.models.DOWNLOAD_ENTITY_1_TITLE
import com.smascaro.trackmixing.common.models.TRACK_VIDEO_KEY
import com.smascaro.trackmixing.common.models.TestModels
import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.main.components.bottomplayer.model.BottomPlayerData
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BottomPlayerControllerTest {
    private lateinit var SUT: BottomPlayerController

    // region constants
    // endregion constants

    // region helper fields
    @Mock private lateinit var playbackStateManager: PlaybackStateManager
    private lateinit var navigationHelper: NavigationHelperTd
    private lateinit var viewMvc: BottomPlayerViewMvcTd
    private lateinit var eventBus: EventBusTd
    private lateinit var tracksRepository: TracksRepository
    // endregion helper fields

    @Before
    fun setup() {
        viewMvc = BottomPlayerViewMvcTd()
        eventBus = EventBusTd()
        tracksRepository = TracksRepositoryTd()
        navigationHelper = NavigationHelperTd()

        SUT = BottomPlayerController(
            playbackStateManager,
            tracksRepository,
            eventBus,
            navigationHelper
        )
        SUT.bindViewMvc(viewMvc)
    }

    @After
    fun validate() {
        validateMockitoUsage()
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
    fun onDestroy_eventBusListenerIsUnregistered() {
        // Arrange
        // Act
        SUT.onDestroy()
        // Assert
        assertTrue(eventBus.isListenerUnregistered)
        assertEquals(SUT, eventBus.unregisteredListener)
    }

    @Test
    fun onDestroy_viewMvcListenerIsUnregistered() {
        // Arrange
        // Act
        SUT.onDestroy()
        // Assert
        assertEquals(SUT, viewMvc.unregisteredListener)
    }

    @Test
    fun onLayoutClick_userClicksOnPlayerLayout_stateIsPlaying_navigatesToPlayerScreen() {
        // Arrange
        setPlayingState()
        setPlaybackStateCurrentSongId()
        // Act
        SUT.onPlayerStateChanged()
        SUT.onLayoutClick()
        // Assert
        assertTrue(navigationHelper.hasNavigatedToPlayer)
    }

    @Test
    fun onLayoutClick_userClicksOnPlayerLayout_stateIsPaused_navigatesToPlayerScreen() {
        // Arrange
        setPausedState()
        setPlaybackStateCurrentSongId()
        // Act
        SUT.onPlayerStateChanged()
        SUT.onLayoutClick()
        // Assert
        assertTrue(navigationHelper.hasNavigatedToPlayer)
    }

    @Test
    fun onLayoutClick_userClicksOnPlayerLayout_stateIsStopped_navigatesToPlayerScreen() {
        // Arrange
        setStoppedState()
        setPlaybackStateCurrentSongId()
        // Act
        SUT.onPlayerStateChanged()
        SUT.onLayoutClick()
        // Assert
        assertFalse(navigationHelper.hasNavigatedToPlayer)
    }

    @Test
    fun onActionButtonClicked_userClicksOnActionButton_stateIsPlaying_pauseMasterEventIsPosted() {
        // Arrange
        setPlayingState()
        // Act
        SUT.onActionButtonClicked()
        // Assert
        assertEquals(
            PlaybackEvent.PauseMasterEvent::class.java,
            eventBus.lastPostedEvent?.javaClass
        )
    }

    @Test
    fun onActionButtonClicked_userClicksOnActionButton_stateIsPaused_playMasterEventIsPosted() {
        // Arrange
        setPausedState()
        // Act
        SUT.onActionButtonClicked()
        // Assert
        assertEquals(PlaybackEvent.PlayMasterEvent::class.java, eventBus.lastPostedEvent?.javaClass)
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
        val event = PlaybackEvent.TimestampChanged(120, 240)
        // Act
        SUT.onMessageEvent(event)
        // Assert
        assertEquals(0.5f, viewMvc.percentageToUpdate)
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

    }

    class NavigationHelperTd : NavigationHelper {
        var hasNavigatedToPlayer: Boolean = false
        override fun navigateTo(destination: Int) {
        }

        override fun bindNavController(navController: NavController) {
        }

        override fun toDetails(track: Track, extras: FragmentNavigator.Extras) {
        }

        override fun toPlayer(track: Track) {
            hasNavigatedToPlayer = true
        }

        override fun toSearch() {
        }

        override fun back() {
        }

    }

    class BottomPlayerViewMvcTd : BottomPlayerViewMvc {
        var totalInteractions = 0
        var isPlayerBarShown: Boolean = false
        var dataInPlayerBar: BottomPlayerData? = null
        var isPlayButtonShown: Boolean = false
        var isPauseButtonShown: Boolean = false
        var registeredListener: Any? = null
        var unregisteredListener: Any? = null
        var percentageToUpdate: Float = -1.0f
        override fun onCreate() {
            totalInteractions++
        }

        override fun showPlayerBar(data: BottomPlayerData) {
            dataInPlayerBar = data
            isPlayerBarShown = true
            totalInteractions++
        }

        override fun hidePlayerBar() {
            dataInPlayerBar = null
            isPlayerBarShown = false
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

        override fun updateTimestamp(percentage: Float) {
            percentageToUpdate = percentage
            totalInteractions++
        }

        override fun registerListener(listener: BottomPlayerViewMvc.Listener) {
            registeredListener = listener
            totalInteractions++
        }

        override fun unregisterListener(listener: BottomPlayerViewMvc.Listener) {
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