package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.di.settings.SettingsComponentProvider
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.controller.SelectTestDataController
import javax.inject.Inject

class SelectTestDataFragment : Fragment() {
    @Inject
    lateinit var controller: SelectTestDataController
    @Inject
    lateinit var viewMvc: SelectTestDataViewMvc
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as SettingsComponentProvider).provideSettingsComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_select_test_data, null, false))
        controller.bindViewMvc(viewMvc)
        controller.bindNavController(findNavController())
        return viewMvc.getRootView()
    }

    override fun onStart() {
        super.onStart()
        controller.onStart()
    }

    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.dispose()
    }
}