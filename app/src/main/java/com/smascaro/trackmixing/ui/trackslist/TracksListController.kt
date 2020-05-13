package com.smascaro.trackmixing.ui.trackslist

import android.os.Environment
import com.smascaro.trackmixing.tracks.DownloadTrackUseCase
import com.smascaro.trackmixing.tracks.FetchAvailableTracksUseCase
import com.smascaro.trackmixing.tracks.Track
import timber.log.Timber
import java.io.File


class TracksListController(
    private val mFetchAvailableTracksUseCase: FetchAvailableTracksUseCase,
    private val mDownloadTrackUseCase: DownloadTrackUseCase
) : TracksListViewMvc.Listener,
    FetchAvailableTracksUseCase.Listener,
    DownloadTrackUseCase.Listener {
    private lateinit var mViewMvc: TracksListViewMvc
    override fun onTrackClicked(track: Track) {
        Timber.i("Track clicked: ${track.title}")
        Timber.i("Track clicked: ${track.title}")
        mDownloadTrackUseCase.downloadTrackAndNotify(track)

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
        val downloadedFile = File(path)
        Timber.d("List of files in path $path:")
        File(downloadedFile.parent!!).listFiles()?.forEach {
            Timber.d("${it?.absoluteFile}")
        }
    }

    override fun onDownloadTrackError() {
        Timber.i("Download of track FAILED")
    }
}