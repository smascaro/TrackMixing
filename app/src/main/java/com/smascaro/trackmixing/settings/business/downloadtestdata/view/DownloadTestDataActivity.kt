package com.smascaro.trackmixing.settings.business.downloadtestdata.view

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseActivity

class DownloadTestDataActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_test_data)
        title = "Confirm download"
    }
}