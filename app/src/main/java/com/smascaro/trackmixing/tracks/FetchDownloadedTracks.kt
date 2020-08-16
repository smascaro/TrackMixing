package com.smascaro.trackmixing.tracks

import com.smascaro.trackmixing.data.DownloadsDao
import com.smascaro.trackmixing.data.toModel
import com.smascaro.trackmixing.ui.common.BaseObservable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FetchDownloadedTracks @Inject constructor(private val mDao: DownloadsDao) :
    BaseObservable<FetchDownloadedTracks.Listener>() {
    interface Listener {
        fun onTracksFetched(tracks: List<Track>)
    }

    enum class Sort {
        NEWEST_FIRST, OLDEST_FIRST, ALPHABETICALLY_ASC, ALPHABETICALLY_DESC, LONGEST_FIRST, SHORTEST_FIRST
    }

    fun fetchTracksAndNotify(criteria: Sort) {
        GlobalScope.launch {
            var tracks = mDao.getAll().map { it.toModel() }
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