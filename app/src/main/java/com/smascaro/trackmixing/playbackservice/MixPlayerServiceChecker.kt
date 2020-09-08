package com.smascaro.trackmixing.playbackservice

import android.app.ActivityManager
import android.content.Context
import javax.inject.Inject

class MixPlayerServiceChecker @Inject constructor(private val context: Context) {
    fun ping(): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.getRunningServices(Int.MAX_VALUE).any {
            it.service.className == MixPlayerService::class.java.name
        }
    }
}