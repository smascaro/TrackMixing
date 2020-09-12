package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.controller

import com.smascaro.trackmixing.common.controller.BaseNavigatorController
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view.SelectTestDataViewMvc
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase
import javax.inject.Inject

class SelectTestDataController @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    p_navigationHelper: NavigationHelper
) :
    BaseNavigatorController<SelectTestDataViewMvc>(p_navigationHelper) {
    fun onStart() {
        downloadTestDataUseCase.getTestDataBundleInfo {
            when (it) {
                is DownloadTestDataUseCase.Result.Success -> viewMvc.bindTracks(it.tracks)
                is DownloadTestDataUseCase.Result.Failure -> viewMvc.showError(it.throwable.localizedMessage)
            }
        }
    }
}