package com.smascaro.trackmixing.settings.testdata.download.view

import android.os.Bundle
import androidx.navigation.findNavController
import com.smascaro.trackmixing.base.ui.BaseActivity
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.settings.R
import com.smascaro.trackmixing.settings.di.component.SettingsComponentProvider
import javax.inject.Inject

class DownloadTestDataActivity : BaseActivity() {
    @Inject
    lateinit var navigationHelper: NavigationHelper

    interface BackPressedListener {
        fun onBackPressed(): Boolean
    }

    private var listener: BackPressedListener? = null

    fun setOnBackPressedListener(listener: BackPressedListener) {
        this.listener = listener
    }

    fun removeOnBackPressedListener(listener: BackPressedListener) {
        this.listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as SettingsComponentProvider).provideSettingsComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_test_data)
        title = "Confirm download"
        navigationHelper.bindNavController(findNavController(R.id.nav_host_fragment_download_test_data))
    }

    override fun onBackPressed() {
        val consumedByListener = listener?.onBackPressed() ?: false
        if (!consumedByListener && !navigationHelper.backAndPop()) {
            super.onBackPressed()
        }
    }
}