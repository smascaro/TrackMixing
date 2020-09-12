package com.smascaro.trackmixing.settings.view

import android.os.Bundle
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseActivity

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_fragment_container, SettingsFragment.create())
            .commit()
    }
}