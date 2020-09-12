package com.smascaro.trackmixing.settings.business.view

import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_confirm_download_test_data.*

class ConfirmDownloadTestDataActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Confirm download"
        setContentView(R.layout.activity_confirm_download_test_data)
        val btnConfirmationOk = findViewById<MaterialButton>(R.id.btn_confirm_download_test_data_ok)
        btnConfirmationOk.setOnClickListener {
            Toast.makeText(this, "Start downloading files", Toast.LENGTH_SHORT).show()
        }
        val btnConfirmationCancel =
            findViewById<MaterialButton>(R.id.btn_confirm_download_test_data_cancel)
        btnConfirmationCancel.setOnClickListener {
            finish()
        }
    }
}