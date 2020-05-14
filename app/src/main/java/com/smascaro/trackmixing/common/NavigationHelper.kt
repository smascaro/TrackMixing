package com.smascaro.trackmixing.common

import android.content.Context
import android.content.Intent
import com.smascaro.trackmixing.TracksPlayerActivity

class NavigationHelper(private val mContext: Context) {
    //TODO: update database with path and send only video key
    fun toPlayer(filesPath: String) {
        TracksPlayerActivity.start(mContext, filesPath)
    }
}