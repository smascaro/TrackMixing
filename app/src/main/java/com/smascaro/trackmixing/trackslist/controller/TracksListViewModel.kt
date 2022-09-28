package com.smascaro.trackmixing.trackslist.controller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import com.smascaro.trackmixing.trackslist.model.RefreshListEvent
import com.smascaro.trackmixing.utilities.launchIO
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class TracksListViewModel @Inject constructor(
    private val mFetchDownloadedTracks: FetchDownloadedTracks,
    private val playbackSession: PlaybackSession,
    private val eventBus: EventBus,
) : ViewModel(),
    FetchDownloadedTracks.Listener {

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private fun loadTracks() {
        viewModelScope.launchIO {
            mFetchDownloadedTracks.fetchTracksAndNotify(FetchDownloadedTracks.Sort.ALPHABETICALLY_ASC)
        }
    }

    fun onStart() {
        mFetchDownloadedTracks.registerListener(this)
        eventBus.register(this)
        loadTracks()
    }

    fun onStop() {
        eventBus.unregister(this)
        mFetchDownloadedTracks.unregisterListener(this)
    }

    fun onTrackClicked(track: Track) {
        playbackSession.startPlayback(track)
    }

    override fun onTracksFetched(tracks: List<Track>) {
        _tracks.postValue(tracks)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshListEvent) {
        loadTracks()
    }
}