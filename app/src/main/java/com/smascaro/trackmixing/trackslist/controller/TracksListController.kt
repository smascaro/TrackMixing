package com.smascaro.trackmixing.trackslist.controller

import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import com.smascaro.trackmixing.trackslist.model.RefreshListEvent
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class TracksListController @Inject constructor(
    private val mFetchDownloadedTracks: FetchDownloadedTracks,
    private val playbackSession: PlaybackSession,
    private val eventBus: EventBus,
    navigationHelper: NavigationHelper
) : BaseNavigatorController<TracksListViewMvc>(navigationHelper),
    TracksListViewMvc.Listener,
    FetchDownloadedTracks.Listener {
    interface NavigationListener {
        fun beforeNavigationToSearch()
    }

    var navigationListener: NavigationListener? = null
    fun registerNavigationListener(listener: NavigationListener) {
        navigationListener = listener
    }

    fun unregisterNavigationListener() {
        navigationListener = null
    }

    override fun onSearchNavigationButtonClicked() {
        navigateToSearch()
    }

    private fun navigateToSearch() {
        navigationListener?.beforeNavigationToSearch()
        navigationHelper.toSearch()
    }

    private fun loadTracks() {
        GlobalScope.launch {
            mFetchDownloadedTracks.fetchTracksAndNotify(FetchDownloadedTracks.Sort.ALPHABETICALLY_ASC)
        }
    }

    fun onStart() {
        viewMvc.registerListener(this)
        mFetchDownloadedTracks.registerListener(this)
        eventBus.register(this)
        loadTracks()
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
        eventBus.unregister(this)
        mFetchDownloadedTracks.unregisterListener(this)
    }

    override fun onTrackClicked(track: Track) {
        playbackSession.startPlayback(track)
    }

    override fun onTracksFetched(tracks: List<Track>) {
        viewMvc.bindTracks(tracks)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshListEvent) {
        loadTracks()
    }

    override fun dispose() {
        viewMvc.unregisterListener(this)
    }
}