package com.smascaro.trackmixing.common.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "download", indices = [Index(value = ["sourceVideoKey"], unique = true)])
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val sourceVideoKey: String,
    val quality: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val downloadTimestamp: String,//TODO pasar a millis
    var downloadPath: String,
    var status: Int,
    var secondsLong: Int
) {
    class DownloadStatus {
        companion object {
            const val PENDING: Int = 1
            const val FINISHED: Int = 2
            const val ERROR: Int = -1
        }
    }
}
