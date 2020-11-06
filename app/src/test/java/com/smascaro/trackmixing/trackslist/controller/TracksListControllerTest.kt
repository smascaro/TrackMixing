package com.smascaro.trackmixing.trackslist.controller

import com.smascaro.trackmixing.base.data.repository.toModel
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.common.models.TestModels
import com.smascaro.trackmixing.common.testdoubles.EventBusTd
import com.smascaro.trackmixing.helpers.MockitoHelper
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.validateMockitoUsage
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TracksListControllerTest {
    private lateinit var SUT: TracksListController

    // region constants
    val fetchedTracksList = TestModels.getDownloadEntityList().map { it.toModel() }
    val clickedTrack1 = fetchedTracksList[0]
    val clickedTrack2 = fetchedTracksList[1]
    // endregion constants

    // region helper fields
    @Mock
    private lateinit var viewMvc: TracksListViewMvc
    @Mock
    private lateinit var fetchDownloadedTracksUseCase: FetchDownloadedTracks
    @Mock
    private lateinit var playbackSession: PlaybackSession
    @Mock
    private lateinit var navigationHelper: NavigationHelper
    // endregion helper fields

    @Before
    fun setup() {
        SUT =
            TracksListController(
                fetchDownloadedTracksUseCase,
                playbackSession,
                EventBusTd(),
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
    fun onStart_viewMvcListenerIsRegistered() {
        // Arrange
        // Act
        SUT.onStart()
        // Assert
        verify(viewMvc).registerListener(SUT)
    }

    @Test
    fun onStart_useCaseListenerIsRegistered() {
        // Arrange
        // Act
        SUT.onStart()
        // Assert
        verify(fetchDownloadedTracksUseCase).registerListener(SUT)
    }

    @Test
    fun onStart_tracksAreLoaded() = runBlocking {
        // Arrange
        // Act
        SUT.onStart()
        // Assert
        verify(fetchDownloadedTracksUseCase)
            .fetchTracksAndNotify(MockitoHelper.anyObject())
    }

    @Test
    fun onStop_viewMvcListenerIsUnregistered() {
        // Arrange
        // Act
        SUT.onStop()
        // Assert
        verify(viewMvc).unregisterListener(SUT)
    }

    @Test
    fun onStop_useCaseListenerIsUnregistered() {
        // Arrange
        // Act
        SUT.onStop()
        // Assert
        verify(fetchDownloadedTracksUseCase).unregisterListener(SUT)
    }

    @Test
    fun onSearchButtonClicked_navigatesToSearchFragment() {
        // Arrange
        // Act
        SUT.onSearchNavigationButtonClicked()
        // Assert
        verify(navigationHelper).toSearch()
    }

    @Test
    fun onTrackClicked_startsPlayingTrack() {
        // Arrange
        // Act
        SUT.onTrackClicked(clickedTrack1)
        // Assert
        verify(playbackSession).startPlayback(clickedTrack1)
    }

    @Test
    fun onTrackClicked_secondTime_startsPlayingSecondTrack() {
        // Arrange
        // Act
        SUT.onTrackClicked(clickedTrack1)
        SUT.onTrackClicked(clickedTrack2)
        // Assert
        verify(playbackSession, times(2)).startPlayback(MockitoHelper.anyObject())
        verify(playbackSession).startPlayback(clickedTrack1)
        verify(playbackSession).startPlayback(clickedTrack2)
    }

    @Test
    fun onTracksFetched_viewIsUpdated() {
        // Arrange
        // Act
        SUT.onTracksFetched(fetchedTracksList)
        // Assert
        verify(viewMvc).bindTracks(fetchedTracksList)
    }
    // endregion tests

    // region helper methods
    // endregion helper methods

    // region helper classes
    // endregion helper classes
}