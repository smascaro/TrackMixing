package com.smascaro.trackmixing.settings.business.view

import android.os.Bundle
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseActivity

class ConfirmDownloadTestDataActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Confirm download"
        setContentView(R.layout.activity_confirm_download_test_data)
    }
}