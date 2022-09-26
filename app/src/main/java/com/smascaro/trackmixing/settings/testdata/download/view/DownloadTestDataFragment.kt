package com.smascaro.trackmixing.settings.testdata.download.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.di.component.SettingsComponentProvider
import com.smascaro.trackmixing.settingsOld.testdata.download.controller.DownloadTestDataController
import com.smascaro.trackmixing.settingsOld.testdata.download.view.DownloadTestDataViewMvc
import javax.inject.Inject

class DownloadTestDataFragment : Fragment(), DownloadTestDataController.Listener,
    DownloadTestDataActivity.BackPressedListener {
    @Inject
    lateinit var controller: DownloadTestDataController
    @Inject
    lateinit var viewMvc: DownloadTestDataViewMvc
    private val arguments: DownloadTestDataFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as com.smascaro.trackmixing.di.component.SettingsComponentProvider).provideSettingsComponent().inject(this)
        (activity as DownloadTestDataActivity).setOnBackPressedListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_download_test_data, null, false))
        controller.bindViewMvc(viewMvc)
        controller.bindTracksToDownload(arguments.dataToDownload)
        controller.onCreate()
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        controller.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        controller.unregisterListener()
    }

    override fun onFinishedFlow() {
        controller.unregisterListener()
        activity?.finish()
    }

    override fun onBackPressed(): Boolean {
        return controller.cancelDownloads()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.dispose()
    }
}