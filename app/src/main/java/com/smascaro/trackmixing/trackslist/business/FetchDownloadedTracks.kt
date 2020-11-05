package com.smascaro.trackmixing.trackslist.business

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.data.repository.toModel
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchDownloadedTracks @Inject constructor(
    private val tracksRepository: TracksRepository
) :
    BaseObservable<FetchDownloadedTracks.Listener>() {
    interface Listener {
        fun onTracksFetched(tracks: List<Track>)
    }

    enum class Sort {
        ALPHABETICALLY_ASC, ALPHABETICALLY_DESC, LONGEST_FIRST, SHORTEST_FIRST//,NEWEST_FIRST, OLDEST_FIRST
    }

    suspend fun fetchTracksAndNotify(criteria: Sort) = withContext(Dispatchers.IO) {
        var tracks = tracksRepository.getAll().map { it.toModel() }
        tracks = when (criteria) {
            Sort.ALPHABETICALLY_ASC -> tracks.sortedBy { it.title }
            Sort.ALPHABETICALLY_DESC -> tracks.sortedByDescending { it.title }
            Sort.LONGEST_FIRST -> tracks.sortedByDescending { it.secondsLong.value }
            Sort.SHORTEST_FIRST -> tracks.sortedBy { it.secondsLong.value }
            else -> tracks
        }
        getListeners().forEach {
            it.onTracksFetched(tracks)
        }
    }
}