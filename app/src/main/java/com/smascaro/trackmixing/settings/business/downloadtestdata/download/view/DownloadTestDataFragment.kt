package com.smascaro.trackmixing.settings.business.downloadtestdata.download.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.settings.business.downloadtestdata.DownloadTestDataActivity
import com.smascaro.trackmixing.settings.business.downloadtestdata.download.controller.DownloadTestDataController
import javax.inject.Inject

class DownloadTestDataFragment : Fragment(), DownloadTestDataController.Listener {

    @Inject lateinit var controller: DownloadTestDataController
    @Inject lateinit var viewMvc: DownloadTestDataViewMvc

    private val arguments: DownloadTestDataFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as DownloadTestDataActivity).settingsComponent.inject(this)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(
            context,
            "Downloading ${arguments.dataToDownload.size} items",
            Toast.LENGTH_SHORT
        ).show()
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
}