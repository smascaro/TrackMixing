package com.smascaro.trackmixing.common.models

import com.smascaro.trackmixing.common.data.model.DownloadEntity
import com.smascaro.trackmixing.common.data.model.Track

class TestModels {
    companion object {
        fun getTrack(): Track {
            return Track(
                TRACK_TITLE,
                TRACK_AUTHOR,
                TRACK_VIDEO_KEY,
                TRACK_THUMBNAIL_URL,
                TRACK_SECONDS_LONG,
                TRACK_REQUESTED_TIMESTAMP,
                TRACK_DOWNLOAD_PATH
            )
        }

        fun getDownloadEntity(): DownloadEntity {
            return DownloadEntity(
                DOWNLOAD_ENTITY_ID,
                DOWNLOAD_ENTITY_VIDEO_KEY,
                DOWNLOAD_ENTITY_QUALITY,
                DOWNLOAD_ENTITY_TITLE,
                DOWNLOAD_ENTITY_AUTHOR,
                DOWNLOAD_ENTITY_THUMBNAIL_URL,
                DOWNLOAD_ENTITY_REQUESTED_TIMESTAMP,
                DOWNLOAD_ENTITY_DOWNLOAD_PATH,
                DOWNLOAD_ENTITY_STATUS,
                TRACK_SECONDS_LONG
            )
        }
    }
}