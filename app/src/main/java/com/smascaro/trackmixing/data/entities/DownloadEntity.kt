package com.smascaro.trackmixing.data.entities

import android.app.PendingIntent
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "download")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val sourceVideoKey: String,
    val quality: String,
    val title: String,
    val thumbnailUrl: String,
    val downloadTimestamp: String,
    var downloadPath: String,
    var status: Int
) {
    class DownloadStatus {
        companion object {
            const val PENDING: Int = 1
            const val FINISHED: Int = 2
        }
    }
}