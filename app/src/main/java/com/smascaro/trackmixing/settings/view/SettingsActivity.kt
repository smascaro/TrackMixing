package com.smascaro.trackmixing.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import com.smascaro.trackmixing.base.ui.BaseActivity
import com.smascaro.trackmixing.R

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_settings, null, false)

        setContentView(rootView)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_settings_container, SettingsFragment.create())
            .commit()
    }
}