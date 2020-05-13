package com.smascaro.trackmixing.common

import android.content.Context
import android.os.Environment

class FilesStorageHelper(private val mContext: Context) {

    fun getBaseDirectoryByVideoId(videoId: String): String {
        return "${mContext.filesDir}/$videoId"
    }

}