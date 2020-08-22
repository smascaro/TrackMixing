package com.smascaro.trackmixing.details.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.FilesStorageHelper
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.details.view.TrackDetailsViewMvc
import com.smascaro.trackmixing.player.business.DownloadTrackUseCase
import timber.log.Timber
import javax.inject.Inject

class TrackDetailsController @Inject constructor(
    private val mDownloadTrackUseCase: DownloadTrackUseCase,
    private val mFilesStorageHelper: FilesStorageHelper,
    private val mNavigationHelper: NavigationHelper
) : BaseController<TrackDetailsViewMvc>(), TrackDetailsViewMvc.Listener,
    DownloadTrackUseCase.Listener {

    fun onStart() {
        viewMvc.registerListener(this)
        mDownloadTrackUseCase.registerListener(this)
    }

    fun onStop() {
        viewMvc.unregisterListener(this)
        mDownloadTrackUseCase.unregisterListener(this)
    }

    fun initUI() {
        viewMvc.initUI()
    }

    override fun onGoToPlayerButtonClicked(track: Track) {
        mNavigationHelper.toPlayer(track)
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