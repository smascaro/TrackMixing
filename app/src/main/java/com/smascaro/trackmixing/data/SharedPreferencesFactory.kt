package com.smascaro.trackmixing.data

import android.content.Context
import android.content.SharedPreferences
import com.smascaro.trackmixing.common.SHARED_PREFERENCES_PLAYBACK

class SharedPreferencesFactory {
    companion object {
        fun getPlaybackSharedPreferencesFactory(context: Context): SharedPreferences {
            return context.getSharedPreferences(SHARED_PREFERENCES_PLAYBACK, Context.MODE_PRIVATE)
        }
    }
}