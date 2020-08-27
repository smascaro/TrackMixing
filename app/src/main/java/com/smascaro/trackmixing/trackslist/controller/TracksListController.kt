package com.smascaro.trackmixing.trackslist.controller


import androidx.navigation.fragment.FragmentNavigatorExtras
import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.player.business.DownloadTrackUseCase
import com.smascaro.trackmixing.trackslist.business.FetchAvailableTracksUseCase
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import com.smascaro.trackmixing.trackslist.view.TracksListViewMvc
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class TracksListController @Inject constructor(
    private val mFetchAvailableTracksUseCase: FetchAvailableTracksUseCase,
    private val mDownloadTrackUseCase: DownloadTrackUseCase,
    private val mFetchDownloadedTracks: FetchDownloadedTracks,
    navigationHelper: NavigationHelper
) : BaseNavigatorController<TracksListViewMvc>(navigationHelper),
    TracksListViewMvc.Listener,
    FetchAvailableTracksUseCase.Listener,
    DownloadTrackUseCase.Listener, FetchDownloadedTracks.Listener {

    override fun onCurrentDataSourceRequest(dataSource: TracksListViewMvc.TracksDataSource) {
        loadTracksFrom(dataSource)
    }

    override fun onSearchNavigationButtonClicked() {
        navigateToSearch()
    }

    private fun navigateToSearch() {
        navigationHelper.toSearch()
    }

    fun loadTracksFrom(dataSource: TracksListViewMvc.TracksDataSource) {
        if (dataSource == TracksListViewMvc.TracksDataSource.DATABASE) {
            mFetchDownloadedTracks.fetchTracksAndNotify(FetchDownloadedTracks.Sort.ALPHABETICALLY_ASC)
        } else {
            mFetchAvailableTracksUseCase.fetchAvailableTracksAndNotify()
        }

    }

    fun onStart() {
        viewMvc.registerListener(this)
        mFetchAvailableTracksUseCase.registerListener(this)
        mDownloadTrackUseCase.registerListener(this)
        mFetchDownloadedTracks.registerListener(this)
        loadTracksFrom(viewMvc.getCurrentDataSource())
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
        mFetchAvailableTracksUseCase.unregisterListener(this)
        mDownloadTrackUseCase.unregisterListener(this)
        mFetchDownloadedTracks.unregisterListener(this)
    }

    override fun onTrackClicked(track: Track) {
        navigateToDetails(track)
    }

    override fun onAvailableTracksFetched(tracks: List<Track>) {
        viewMvc.bindTracks(tracks)
    }

    override fun onAvailableTracksFetchFailed() {
        Timber.e("Available tracks fetch failed")
    }

    override fun onDownloadTrackStarted(track: Track) {
        Timber.i("Download of track ${track.videoKey} STARTED")
    }

    override fun onDownloadTrackFinished(track: Track, path: String) {
        Timber.i("Download of track ${track.videoKey} FINISHED")
        val downloadDirectory = File(path)
        Timber.d("List of files in path $path:")
        downloadDirectory.listFiles()?.forEach {
            if (it.isDirectory) {
                Timber.d("Dir: ${it.absolutePath}")
            } else if (it.isFile) {
                Timber.d("File: ${it?.absoluteFile}, size: ${it.length() / 1000}KB (${it.length() / 1000000}MB)")
            }
        }
//        viewMvc.navigateToPlayer(track)
        navigateToPlayer(track)
    }

    fun navigateToPlayer(track: Track) {
        navigationHelper.toPlayer(track)
    }

    fun navigateToDetails(track: Track) {
        navigationHelper.toDetails(track, FragmentNavigatorExtras())
    }

    override fun onDownloadTrackError() {
        Timber.i("Download of track FAILED")
    }

    override fun onTracksFetched(tracks: List<Track>) {
        viewMvc.bindTracks(tracks)
    }
}