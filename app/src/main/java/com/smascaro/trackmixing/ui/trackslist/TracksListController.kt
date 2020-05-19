package com.smascaro.trackmixing.ui.trackslist


import androidx.navigation.NavInflater
import com.smascaro.trackmixing.common.FilesStorageHelper
import com.smascaro.trackmixing.tracks.DownloadTrackUseCase
import com.smascaro.trackmixing.tracks.FetchAvailableTracksUseCase
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper
import timber.log.Timber
import java.io.File


class TracksListController(
    private val mFetchAvailableTracksUseCase: FetchAvailableTracksUseCase,
    private val mDownloadTrackUseCase: DownloadTrackUseCase,
    private val mFilesStorageHelper: FilesStorageHelper,
    private val mNavigationHelper: NavigationHelper
) : TracksListViewMvc.Listener,
    FetchAvailableTracksUseCase.Listener,
    DownloadTrackUseCase.Listener {
    private lateinit var mViewMvc: TracksListViewMvc
    override fun onTrackClicked(track: Track) {
        Timber.i("Track clicked: ${track.title}")
        mDownloadTrackUseCase.downloadTrackAndNotify(
            track,
            mFilesStorageHelper.getBaseDirectory()
        )

//        mViewMvc.displayFloatingCard()
    }

    fun bindView(mvcView: TracksListViewMvc) {
        mViewMvc = mvcView
    }

    fun onStart() {
        mViewMvc.registerListener(this)
        mFetchAvailableTracksUseCase.registerListener(this)
        mDownloadTrackUseCase.registerListener(this)

        mFetchAvailableTracksUseCase.fetchAvailableTracksAndNotify()
    }

    fun onStop() {
        mViewMvc.unregisterListener(this)
        mFetchAvailableTracksUseCase.unregisterListener(this)
        mDownloadTrackUseCase.unregisterListener(this)
    }

    override fun onAvailableTracksFetched(tracks: List<Track>) {
        mViewMvc.bindTracks(tracks)
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
        mViewMvc.navigateToPlayer(path)
    }

    override fun onDownloadTrackError() {
        Timber.i("Download of track FAILED")
    }
}