package com.smascaro.trackmixing.settings.testdata.selection.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.di.ViewModelProviderFactory
import com.smascaro.trackmixing.base.ui.BaseFragment
import com.smascaro.trackmixing.base.utils.asGB
import com.smascaro.trackmixing.base.utils.asKB
import com.smascaro.trackmixing.base.utils.asMB
import com.smascaro.trackmixing.databinding.FragmentSelectTestDataBinding
import com.smascaro.trackmixing.settings.testdata.selection.view.SelectTestDataFragmentDirections.Companion.actionDestinationSelectTestDataToDownloadTestDataFragment
import com.smascaro.trackmixing.utilities.nullifyOnDestroy
import javax.inject.Inject

class SelectTestDataFragment : BaseFragment() {
    private var binding: FragmentSelectTestDataBinding by nullifyOnDestroy()

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private lateinit var viewModel: TestDataViewModel

    private var defaultMaterialTextColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectTestDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity().viewModelStore, providerFactory)[TestDataViewModel::class.java]

        initializeBindings()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.onProgress.observe(viewLifecycleOwner) { show ->
            binding.shimmerContainerTestData.apply {
                if (show) {
                    visibility = View.VISIBLE
                    startShimmer()
                } else {
                    visibility = View.GONE
                    stopShimmer()
                }
            }
        }
        viewModel.onError.observe(viewLifecycleOwner) {
            showMessage(
                when (it) {
                    TestDataViewModel.ErrorType.NO_TRACKS_FOUND -> "No test tracks found."
                }
            )
        }
        viewModel.availableTracks.observe(viewLifecycleOwner) {
            (binding.rvSelectTestDataTracks.adapter as? TestDataListAdapter)?.bindData(it)
        }
        viewModel.bytesToBeDownloaded.observe(viewLifecycleOwner, ::updateSizeToDownload)
        viewModel.availableBytes.observe(viewLifecycleOwner, ::updateAvailableBytes)
        viewModel.onNavigateToDownload.observe(viewLifecycleOwner) {
            navigationHelper.navigate(actionDestinationSelectTestDataToDownloadTestDataFragment())
        }
    }

    private fun initializeBindings() {
        binding.rvSelectTestDataTracks.apply {
            layoutManager = LinearLayoutManager(context)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            setHasFixedSize(true)
            adapter = TestDataListAdapter().apply {
                setAdapterListener { data, checked ->
                    if (checked) {
                        viewModel.onItemSelected(data)
                    } else {
                        viewModel.onItemUnselected(data)
                    }
                }
            }
        }
        defaultMaterialTextColor = binding.tvSelectTestDataTotalSize.textColors.defaultColor
        binding.btnSelectTestDataStartDownload.setOnClickListener { viewModel.onDownloadButtonClicked() }
        binding.tvSelectTestDataAvailableSpace.text = ""
        updateTotalSize(0)
    }

    private fun updateAvailableBytes(bytes: Long) {
        val text = when {
            bytes > 1 * 1000 * 1000 * 1000 -> bytes.asGB
            bytes > 1 * 1000 * 1000 -> bytes.asMB
            bytes > 1 * 1000 -> bytes.asKB
            else -> "$bytes bytes"
        }

        binding.tvSelectTestDataAvailableSpace.text =
            getString(R.string.select_test_data_available_space, text)
    }

    private fun updateSizeToDownload(bytesToDownload: Long) {
        updateTotalSize(bytesToDownload)
        val availableBytes = viewModel.availableBytes.value ?: Long.MAX_VALUE
        val textColor = if (bytesToDownload > availableBytes) Color.RED else defaultMaterialTextColor
        binding.tvSelectTestDataTotalSize.setTextColor(textColor)
    }

    private fun updateTotalSize(bytes: Long) {
        binding.tvSelectTestDataTotalSize.text = bytes.asMB
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}