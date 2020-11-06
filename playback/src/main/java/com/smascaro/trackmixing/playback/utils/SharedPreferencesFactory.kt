package com.smascaro.trackmixing.playback.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesFactory {
    companion object {
        fun getPlaybackSharedPreferencesFactory(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK,
                Context.MODE_PRIVATE
            )
        }
    }
}