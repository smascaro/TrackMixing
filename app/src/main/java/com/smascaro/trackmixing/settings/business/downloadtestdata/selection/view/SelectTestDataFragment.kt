package com.smascaro.trackmixing.settings.business.downloadtestdata.selection.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.settings.business.downloadtestdata.DownloadTestDataActivity
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.controller.SelectTestDataController
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase.Result.Failure
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.DownloadTestDataUseCase.Result.Success
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SelectTestDataFragment : Fragment() {
    @Inject lateinit var controller: SelectTestDataController
    @Inject lateinit var viewMvc: SelectTestDataViewMvc
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as DownloadTestDataActivity).settingsComponent.inject(this)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val client = OkHttpClient.Builder().apply {
            readTimeout(10L, TimeUnit.SECONDS)
        }.build()
        val retrofit = Retrofit.Builder().apply {
            baseUrl("https://drive.google.com/")
            client(client)
        }.build()
        val usecase = DownloadTestDataUseCase(retrofit.create(TestDataApi::class.java))
        usecase.getTestDataBundleInfo {
            when (it) {
                is Success -> {
                    Timber.d("${it.tracks}")
                }
                is Failure -> Timber.e(it.throwable.localizedMessage)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        controller.onStart()
    }
}