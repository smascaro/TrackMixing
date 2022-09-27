package com.smascaro.trackmixing.settings.testdata.selection.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.settings.testdata.download.DownloadTestDataUseCase
import com.smascaro.trackmixing.settings.testdata.selection.controller.DiskSpaceHelper
import com.smascaro.trackmixing.settings.testdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.utilities.SingleLiveEvent
import com.smascaro.trackmixing.utilities.launchIO
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class TestDataViewModel @Inject constructor(
    private val downloadTestDataUseCase: DownloadTestDataUseCase,
    private val tracksRepository: TracksRepository,
    private val diskSpaceHelper: DiskSpaceHelper
) : ViewModel() {
    val onProgress = SingleLiveEvent<Boolean>()
    val onError = SingleLiveEvent<ErrorType>()

    private val _availableTracks = MutableLiveData<List<TestDataBundleInfo>>()
    val availableTracks: LiveData<List<TestDataBundleInfo>> = _availableTracks

    private val _alreadyDownloadedTracks = MutableLiveData<List<TestDataBundleInfo>>()
    val alreadyDownloadedTracks: LiveData<List<TestDataBundleInfo>> = _alreadyDownloadedTracks

    private val _bytesToBeDownloaded = MutableLiveData<Long>()
    val bytesToBeDownloaded: LiveData<Long> = _bytesToBeDownloaded

    private val _availableSpaceBytes = MutableLiveData<Long>()
    val availableBytes: LiveData<Long> = _availableSpaceBytes

    val onNavigateToDownload = SingleLiveEvent<Unit>()

    private val tracksToDownload = mutableListOf<TestDataBundleInfo>()

    private val _downloadProgress = MutableLiveData<DownloadProgressState>()
    val downloadProgress: LiveData<DownloadProgressState> = _downloadProgress

    val onDownloadsFinished = SingleLiveEvent<Unit>()
    val onDownloadsCancelled = SingleLiveEvent<Unit>()

    var cancellationFlag = false
    var finishedDownloads = 0

    fun onStart() {
        viewModelScope.launchIO {
            onProgress.postValue(true)
            val availableBundles = downloadTestDataUseCase.getTestDataBundleInfo()
            onProgress.postValue(false)
            when (availableBundles) {
                is DownloadTestDataUseCase.Result.Failure -> onError.postValue(ErrorType.NO_TRACKS_FOUND)
                is DownloadTestDataUseCase.Result.Success -> {
                    val downloads = tracksRepository.getAll()
                    availableBundles.tracks.map { track ->
                        track.isPresentInDatabase = downloads.any { it.sourceVideoKey == track.videoKey }
                    }
                    _availableTracks.postValue(availableBundles.tracks)
                }
            }
        }
        _availableSpaceBytes.value = diskSpaceHelper.getAvailableBytes()
    }

    fun onItemSelected(item: TestDataBundleInfo) {
        tracksToDownload.add(item)
        val previousValue = _bytesToBeDownloaded.value ?: 0L
        _bytesToBeDownloaded.value = previousValue + item.size
    }

    fun onItemUnselected(item: TestDataBundleInfo) {
        tracksToDownload.remove(item)
        val previousValue = _bytesToBeDownloaded.value ?: 0L
        _bytesToBeDownloaded.value = (previousValue - item.size).coerceAtLeast(0)
    }

    fun onDownloadButtonClicked() {
        onNavigateToDownload.call()
    }

    fun startDownloads() {
        finishedDownloads = 0
        _downloadProgress.value = DownloadProgressState(finishedDownloads, tracksToDownload.size)
        downloadTestDataUseCase.registerListener(object : DownloadTestDataUseCase.Listener {
            override fun onFinishedItemDownload(videoKey: String) {
                finishedDownloads++
                Timber.w("onFinishedItemDownload \"$videoKey\" - finished (incl.) = $finishedDownloads out of ${tracksToDownload.size} | cancelled=$cancellationFlag")
                if (!cancellationFlag) {
                    _downloadProgress.value = _downloadProgress.value?.copy(downloaded = finishedDownloads)
                }
                if (finishedDownloads == tracksToDownload.size) {
                    if (cancellationFlag) {
                        downloadTestDataUseCase.rollbackDownloads(tracksToDownload)
                    }
                    viewModelScope.launchIO {
                        delay(300)
                        onDownloadsFinished.postValue(Unit)
                    }
                }
            }

            override fun onItemDownloadFailed(trackId: String, throwable: Throwable) {
                finishedDownloads++
                Timber.w("onItemDownloadFailed \"$trackId\" - finished (incl.) = $finishedDownloads out of ${tracksToDownload.size} | cancelled=$cancellationFlag")
                onError.value = ErrorType.TRACK_DOWNLOAD_FAILED
                _downloadProgress.value = _downloadProgress.value?.copy(downloaded = finishedDownloads)
            }
        })
        tracksToDownload.forEach {
            downloadTestDataUseCase.downloadItemBundle(it)
        }
    }

    fun cancelDownloads() {
        downloadTestDataUseCase.markForCancellation()
        onDownloadsCancelled.call()
        cancellationFlag = true
    }

    enum class ErrorType {
        NO_TRACKS_FOUND, TRACK_DOWNLOAD_FAILED
    }

    data class DownloadProgressState(val downloaded: Int, val totalDownloads: Int)
}