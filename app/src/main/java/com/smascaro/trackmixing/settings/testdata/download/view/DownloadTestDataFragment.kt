package com.smascaro.trackmixing.settings.testdata.download.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.databinding.FragmentDownloadTestDataBinding
import com.smascaro.trackmixing.di.ViewModelProviderFactory
import com.smascaro.trackmixing.settings.testdata.selection.view.TestDataViewModel
import com.smascaro.trackmixing.utilities.nullifyOnDestroy
import javax.inject.Inject

class DownloadTestDataFragment : BaseFragment(), DownloadTestDataActivity.BackPressedListener {

    private var binding: FragmentDownloadTestDataBinding by nullifyOnDestroy()

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var viewModel: TestDataViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as? DownloadTestDataActivity)?.setOnBackPressedListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadTestDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity().viewModelStore, providerFactory)[TestDataViewModel::class.java]

        viewModel.startDownloads()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.onError.observe(viewLifecycleOwner) {
            when (it) {
                TestDataViewModel.ErrorType.TRACK_DOWNLOAD_FAILED -> notifyTrackFailure()
                TestDataViewModel.ErrorType.NO_TRACKS_FOUND -> {
                    /* nothing to do */
                }
            }
        }
        viewModel.downloadProgress.observe(viewLifecycleOwner) { (downloaded, total) ->
            binding.tvDownloadTestDataDownloadingProgress.text = "($downloaded/$total)"
            binding.sbDownloadTestDataProgress.max = total
            binding.sbDownloadTestDataProgress.progress = downloaded
        }
        viewModel.onDownloadsCancelled.observe(viewLifecycleOwner) {
            binding.tvDownloadTestDataDownloadingTitle.text = "Cancelling downloads..."
        }
        viewModel.onDownloadsFinished.observe(viewLifecycleOwner) {
            activity?.finish()
        }
    }

    private fun notifyTrackFailure() {
        Toast.makeText(context, "A track failed to download", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed(): Boolean {
        viewModel.cancelDownloads()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // controller.dispose()
    }
}