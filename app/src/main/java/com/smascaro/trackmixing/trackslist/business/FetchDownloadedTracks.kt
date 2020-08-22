package com.smascaro.trackmixing.trackslist.business

import com.smascaro.trackmixing.common.data.datasource.dao.toModel
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchDownloadedTracks @Inject constructor(
    private val tracksRepository: TracksRepository
) :
    BaseObservable<FetchDownloadedTracks.Listener>() {
    interface Listener {
        fun onTracksFetched(tracks: List<Track>)
    }

    enum class Sort {
        NEWEST_FIRST, OLDEST_FIRST, ALPHABETICALLY_ASC, ALPHABETICALLY_DESC, LONGEST_FIRST, SHORTEST_FIRST
    }

    fun fetchTracksAndNotify(criteria: Sort) {
        GlobalScope.launch {
            var tracks = tracksRepository.getAll().map { it.toModel() }
            tracks = when (criteria) {
                Sort.ALPHABETICALLY_ASC -> tracks.sortedBy { it.title }
                Sort.ALPHABETICALLY_DESC -> tracks.sortedByDescending { it.title }
                Sort.LONGEST_FIRST -> tracks.sortedByDescending { it.secondsLong }
                Sort.SHORTEST_FIRST -> tracks.sortedBy { it.secondsLong }
                else -> tracks
            }
            getListeners().forEach {
                it.onTracksFetched(tracks)
            }
        }

    }
}