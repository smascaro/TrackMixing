package com.smascaro.trackmixing.settings.view

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.settings.testdata.download.view.DownloadTestDataActivity

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        fun create(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val pref=findPreference<Preference>("PREFERENCE_DOWNLOAD_TEST_DATA")
        pref?.setOnPreferenceClickListener {
            val intent = Intent(context, DownloadTestDataActivity::class.java)
            startActivity(intent)
            true
        }
    }


}