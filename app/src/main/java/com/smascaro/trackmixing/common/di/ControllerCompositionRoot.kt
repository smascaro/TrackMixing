package com.smascaro.trackmixing.common.di

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.navigation.NavController
import com.smascaro.trackmixing.common.FilesStorageHelper
import com.smascaro.trackmixing.data.DownloadsDatabase
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.networking.NodeDownloadsApi
import com.smascaro.trackmixing.ui.player.TracksPlayerController
import com.smascaro.trackmixing.tracks.DownloadTrackUseCase
import com.smascaro.trackmixing.tracks.FetchAvailableTracksUseCase
import com.smascaro.trackmixing.tracks.FetchDownloadedTracks
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.ViewMvcFactory
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper
import com.smascaro.trackmixing.ui.details.TrackDetailsController
import com.smascaro.trackmixing.ui.main.MainActivityController
import com.smascaro.trackmixing.ui.trackslist.TracksListController

class ControllerCompositionRoot(
    private val mCompositionRoot: CompositionRoot,
    private val mActivity: Activity
) {
    private fun getContext(): Context {
        return mActivity
    }

    private fun getLayoutInflater(): LayoutInflater {
        return LayoutInflater.from(getContext())
    }

    fun getViewMvcFactory(): ViewMvcFactory {
        return ViewMvcFactory(getLayoutInflater())
    }

    fun getTracksListController(navController: NavController): TracksListController {
        return TracksListController(
            getFetchAvailableTracksUseCase(),
            getDownloadTrackUseCase(),
            getFetchDownloadedTracksUseCase()
        )
    }

    fun getNavigationHelper(navController: NavController): NavigationHelper {
        return NavigationHelper(navController)
    }

    private fun getFilesStorageHelper(): FilesStorageHelper {
        return FilesStorageHelper(getContext())
    }

    private fun getDownloadTrackUseCase(): DownloadTrackUseCase {
        return DownloadTrackUseCase(
            getNodeDownloadsApi(),
            getDatabase().getDao(),
            getFilesStorageHelper()
        )
    }

    private fun getFetchDownloadedTracksUseCase(): FetchDownloadedTracks {
        return FetchDownloadedTracks(getDatabase().getDao())
    }

    private fun getDatabase(): DownloadsDatabase {
        return DownloadsDatabase.getDatabase(getContext())
    }

    private fun getFetchAvailableTracksUseCase(): FetchAvailableTracksUseCase {
        return FetchAvailableTracksUseCase(getNodeApi())
    }

    private fun getNodeApi(): NodeApi {
        return mCompositionRoot.getNodeApi()
    }

    private fun getNodeDownloadsApi(): NodeDownloadsApi {
        return mCompositionRoot.getNodeDownloadsApi()
    }

    fun getTrackDetailsController(navController: NavController): TrackDetailsController {
        return TrackDetailsController(
            getDownloadTrackUseCase(),
            getFilesStorageHelper(),
            getNavigationHelper(navController)

        )
    }

    fun getMainActivityController(): MainActivityController {
        return MainActivityController(getNodeApi())
    }


}