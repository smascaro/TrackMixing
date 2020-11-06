package com.smascaro.trackmixing.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import com.smascaro.trackmixing.base.ui.BaseActivity
import com.smascaro.trackmixing.settings.R
import com.smascaro.trackmixing.settings.controller.SettingsActivityController
import com.smascaro.trackmixing.settings.di.component.SettingsComponentProvider
import javax.inject.Inject

class SettingsActivity : BaseActivity() {
    @Inject
    lateinit var viewMvc: SettingsActivityViewMvc
    @Inject
    lateinit var controller: SettingsActivityController

    override fun onCreate(savedInstanceState: Bundle?) {
        // (application as AppComponentProvider).provideAppComponent().inject(this)
        (application as SettingsComponentProvider).provideSettingsComponent().inject(this)
        // (application as TrackMixingApplication)
        //     .appComponent
        //     .settingsComponent()
        //     .create()
        //     .inject(this)
        super.onCreate(savedInstanceState)
        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_settings, null, false)
        viewMvc.bindRootView(rootView)

        setContentView(rootView)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_settings_container, SettingsFragment.create())
            .commit()
    }
}