package com.smascaro.trackmixing.common.di

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import com.smascaro.trackmixing.common.FilesStorageHelper
import com.smascaro.trackmixing.data.DownloadsDatabase
import com.smascaro.trackmixing.networking.NodeApi
import com.smascaro.trackmixing.networking.NodeDownloadsApi
import com.smascaro.trackmixing.tracks.DownloadTrackUseCase
import com.smascaro.trackmixing.tracks.FetchAvailableTracksUseCase
import com.smascaro.trackmixing.ui.common.ViewMvcFactory
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

    fun getTracksListController(): TracksListController {
        return TracksListController(
            getFetchAvailableTracksUseCase(),
            getDownloadTrackUseCase(),
            getFilesStorageHelper()
        )
    }

    private fun getFilesStorageHelper(): FilesStorageHelper {
        return FilesStorageHelper(getContext())
    }
    private fun getDownloadTrackUseCase(): DownloadTrackUseCase {
        return DownloadTrackUseCase(getNodeDownloadsApi(), getDatabase().getDao())
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


}