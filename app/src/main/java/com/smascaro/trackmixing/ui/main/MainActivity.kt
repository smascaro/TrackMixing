package com.smascaro.trackmixing.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (intent.action == Intent.ACTION_SEND) {
            val url = if (intent.clipData != null && intent.clipData!!.itemCount > 0) {
                intent.clipData?.getItemAt(0)!!.text
            } else ""
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
            Timber.d(intent.toString())

        }
    }
}
