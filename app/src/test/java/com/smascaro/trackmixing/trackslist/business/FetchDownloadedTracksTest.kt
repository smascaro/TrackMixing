package com.smascaro.trackmixing.trackslist.business

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.common.models.TestModels
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FetchDownloadedTracksTest {
    private lateinit var SUT: FetchDownloadedTracks

    // region constants
    val testTracksList = TestModels.getDownloadEntityList()
    val maxLength = testTracksList.maxByOrNull { it.secondsLong }?.secondsLong ?: -1
    val minLength = testTracksList.minByOrNull { it.secondsLong }?.secondsLong ?: Int.MAX_VALUE
    val firstAlphabetically = testTracksList.minByOrNull { it.title }?.title
    val lastAlphabetically = testTracksList.maxByOrNull { it.title }?.title
    // endregion constants

    // region helper fields
    @Mock
    private lateinit var tracksRepository: TracksRepository
    private lateinit var listener: FetchDownloadedTracksUseCaseListenerImpl
    // endregion helper fields

    @Before
    fun setup() {
        listener = FetchDownloadedTracksUseCaseListenerImpl()
        SUT = FetchDownloadedTracks(tracksRepository)
    }

    // region tests
    @Test
    fun fetchTracksAndNotify_listenerIsNotified() = runBlocking {
        // Arrange
        mockTrackRepositoryReturn()
        SUT.registerListener(listener)
        val criteria = FetchDownloadedTracks.Sort.LONGEST_FIRST
        // Act
        SUT.fetchTracksAndNotify(criteria)
        // Assert
        Assert.assertNotNull(listener.lastFetchedTracks)
    }

    @Test
    fun fetchTracksAndNotify_sortedByLengthDesc_firstItemHasMaxLength() = runBlocking {
        // Arrange
        mockTrackRepositoryReturn()
        SUT.registerListener(listener)
        val criteria = FetchDownloadedTracks.Sort.LONGEST_FIRST
        // Act
        SUT.fetchTracksAndNotify(criteria)
        // Assert
        Assert.assertEquals(maxLength, listener.lastFetchedTracks!![0].secondsLong)
    }

    @Test
    fun fetchTracksAndNotify_sortedByLengthAsc_firstItemHasMinLength() = runBlocking {
        // Arrange
        mockTrackRepositoryReturn()
        SUT.registerListener(listener)
        val criteria = FetchDownloadedTracks.Sort.SHORTEST_FIRST
        // Act
        SUT.fetchTracksAndNotify(criteria)
        // Assert
        Assert.assertEquals(minLength, listener.lastFetchedTracks!![0].secondsLong)
    }

    @Test
    fun fetchTracksAndNotify_sortedByAlphabeticAsc_firstItemIsFirstAlphabetically() = runBlocking {
        // Arrange
        mockTrackRepositoryReturn()
        SUT.registerListener(listener)
        val criteria = FetchDownloadedTracks.Sort.ALPHABETICALLY_ASC
        // Act
        SUT.fetchTracksAndNotify(criteria)
        // Assert
        Assert.assertEquals(firstAlphabetically, listener.lastFetchedTracks!![0].title)
    }

    @Test
    fun fetchTracksAndNotify_sortedByAlphabeticDesc_firstItemIsLastAlphabetically() = runBlocking {
        // Arrange
        mockTrackRepositoryReturn()
        SUT.registerListener(listener)
        val criteria = FetchDownloadedTracks.Sort.ALPHABETICALLY_DESC
        // Act
        SUT.fetchTracksAndNotify(criteria)
        // Assert
        Assert.assertEquals(lastAlphabetically, listener.lastFetchedTracks!![0].title)
    }
    // endregion tests

    // region helper methods
    fun mockTrackRepositoryReturn() {
        runBlocking {
            Mockito.`when`(tracksRepository.getAll()).thenReturn(testTracksList)
        }
    }
    // endregion helper methods

    // region helper classes
    class FetchDownloadedTracksUseCaseListenerImpl : FetchDownloadedTracks.Listener {
        var lastFetchedTracks: List<Track>? = null
        override fun onTracksFetched(tracks: List<Track>) {
            lastFetchedTracks = tracks
        }
    }
    // endregion helper classes
}