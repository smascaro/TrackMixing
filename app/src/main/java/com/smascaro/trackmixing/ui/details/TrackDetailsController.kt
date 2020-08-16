package com.smascaro.trackmixing.ui.details

import com.smascaro.trackmixing.common.FilesStorageHelper
import com.smascaro.trackmixing.tracks.DownloadTrackUseCase
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper
import timber.log.Timber
import javax.inject.Inject

class TrackDetailsController @Inject constructor(
    private val mDownloadTrackUseCase: DownloadTrackUseCase,
    private val mFilesStorageHelper: FilesStorageHelper,
    private val mNavigationHelper: NavigationHelper
) : TrackDetailsViewMvc.Listener, DownloadTrackUseCase.Listener {
    private lateinit var mViewMvc: TrackDetailsViewMvc

    fun bindView(viewMvc: TrackDetailsViewMvc) {
        mViewMvc = viewMvc
    }

    fun onStart() {
        mViewMvc.registerListener(this)
        mDownloadTrackUseCase.registerListener(this)
    }

    fun onStop() {
        mViewMvc.unregisterListener(this)
        mDownloadTrackUseCase.unregisterListener(this)
    }

    fun initUI() {
        mViewMvc.initUI()
    }

    override fun onGoToPlayerButtonClicked(track: Track) {
        mDownloadTrackUseCase.downloadTrackAndNotify(track, mFilesStorageHelper.getBaseDirectory())
    }

    override fun onDownloadTrackStarted(mTrack: Track) {

    }

    override fun onDownloadTrackFinished(track: Track, path: String) {
        mNavigationHelper.toPlayer(track)
    }

    override fun onDownloadTrackError() {
        Timber.e("Error on DownloadTracksUseCase")
    }
}