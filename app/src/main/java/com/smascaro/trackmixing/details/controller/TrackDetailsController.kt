package com.smascaro.trackmixing.details.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
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
    mNavigationHelper: NavigationHelper
) : BaseNavigatorController<TrackDetailsViewMvc>(mNavigationHelper), TrackDetailsViewMvc.Listener,
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
        navigationHelper.toPlayer(track)
    }

    override fun onDownloadTrackStarted(mTrack: Track) {

    }

    override fun onDownloadTrackFinished(track: Track, path: String) {
        navigationHelper.toPlayer(track)
    }

    override fun onDownloadTrackError() {
        Timber.e("Error on DownloadTracksUseCase")
    }
}