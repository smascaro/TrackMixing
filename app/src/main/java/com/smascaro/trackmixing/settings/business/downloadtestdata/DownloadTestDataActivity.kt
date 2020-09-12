package com.smascaro.trackmixing.settings.business.downloadtestdata

import android.os.Bundle
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.di.settings.SettingsComponent
import com.smascaro.trackmixing.common.view.ui.BaseActivity

class DownloadTestDataActivity : BaseActivity() {
    lateinit var settingsComponent: SettingsComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        settingsComponent =
            (application as TrackMixingApplication).appComponent.settingsComponent().create()
        settingsComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_test_data)
        title = "Confirm download"
    }
}