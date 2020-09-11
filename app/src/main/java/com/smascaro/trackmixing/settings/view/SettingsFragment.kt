package com.smascaro.trackmixing.settings.view

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.smascaro.trackmixing.R

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        fun create(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}