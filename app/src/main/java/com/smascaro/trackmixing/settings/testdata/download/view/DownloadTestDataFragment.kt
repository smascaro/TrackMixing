package com.smascaro.trackmixing.settings.testdata.download.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.di.ViewModelProviderFactory
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.settings.testdata.selection.view.TestDataViewModel
import com.smascaro.trackmixing.settingsOld.testdata.download.controller.DownloadTestDataController
import com.smascaro.trackmixing.settingsOld.testdata.download.view.DownloadTestDataViewMvc
import javax.inject.Inject

class DownloadTestDataFragment : BaseFragment(), DownloadTestDataController.Listener,
    DownloadTestDataActivity.BackPressedListener {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var viewModel: TestDataViewModel

    @Inject
    lateinit var controller: DownloadTestDataController
    @Inject
    lateinit var viewMvc: DownloadTestDataViewMvc

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as DownloadTestDataActivity).setOnBackPressedListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_download_test_data, null, false))
        controller.bindViewMvc(viewMvc)
        controller.onCreate()
        return viewMvc.getRootView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity().viewModelStore, providerFactory)[TestDataViewModel::class.java]

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.tracksToDownload.observe(viewLifecycleOwner){
            controller.bindTracksToDownload(it.toTypedArray())
        }
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