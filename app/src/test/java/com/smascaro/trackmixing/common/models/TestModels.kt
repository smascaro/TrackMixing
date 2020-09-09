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
                DOWNLOAD_ENTITY_1_ID,
                DOWNLOAD_ENTITY_1_VIDEO_KEY,
                DOWNLOAD_ENTITY_1_QUALITY,
                DOWNLOAD_ENTITY_1_TITLE,
                DOWNLOAD_ENTITY_1_AUTHOR,
                DOWNLOAD_ENTITY_1_THUMBNAIL_URL,
                DOWNLOAD_ENTITY_1_REQUESTED_TIMESTAMP,
                DOWNLOAD_ENTITY_1_DOWNLOAD_PATH,
                DOWNLOAD_ENTITY_1_STATUS,
                DOWNLOAD_ENTITY_1_SECONDS_LONG
            )
        }

        fun getDownloadEntityList(): List<DownloadEntity> {
            return listOf(
                DownloadEntity(
                    DOWNLOAD_ENTITY_1_ID,
                    DOWNLOAD_ENTITY_1_VIDEO_KEY,
                    DOWNLOAD_ENTITY_1_QUALITY,
                    DOWNLOAD_ENTITY_1_TITLE,
                    DOWNLOAD_ENTITY_1_AUTHOR,
                    DOWNLOAD_ENTITY_1_THUMBNAIL_URL,
                    DOWNLOAD_ENTITY_1_REQUESTED_TIMESTAMP,
                    DOWNLOAD_ENTITY_1_DOWNLOAD_PATH,
                    DOWNLOAD_ENTITY_1_STATUS,
                    DOWNLOAD_ENTITY_1_SECONDS_LONG
                ),
                DownloadEntity(
                    DOWNLOAD_ENTITY_2_ID,
                    DOWNLOAD_ENTITY_2_VIDEO_KEY,
                    DOWNLOAD_ENTITY_2_QUALITY,
                    DOWNLOAD_ENTITY_2_TITLE,
                    DOWNLOAD_ENTITY_2_AUTHOR,
                    DOWNLOAD_ENTITY_2_THUMBNAIL_URL,
                    DOWNLOAD_ENTITY_2_REQUESTED_TIMESTAMP,
                    DOWNLOAD_ENTITY_2_DOWNLOAD_PATH,
                    DOWNLOAD_ENTITY_2_STATUS,
                    DOWNLOAD_ENTITY_2_SECONDS_LONG
                ),
                DownloadEntity(
                    DOWNLOAD_ENTITY_3_ID,
                    DOWNLOAD_ENTITY_3_VIDEO_KEY,
                    DOWNLOAD_ENTITY_3_QUALITY,
                    DOWNLOAD_ENTITY_3_TITLE,
                    DOWNLOAD_ENTITY_3_AUTHOR,
                    DOWNLOAD_ENTITY_3_THUMBNAIL_URL,
                    DOWNLOAD_ENTITY_3_REQUESTED_TIMESTAMP,
                    DOWNLOAD_ENTITY_3_DOWNLOAD_PATH,
                    DOWNLOAD_ENTITY_3_STATUS,
                    DOWNLOAD_ENTITY_3_SECONDS_LONG
                ),
                DownloadEntity(
                    DOWNLOAD_ENTITY_4_ID,
                    DOWNLOAD_ENTITY_4_VIDEO_KEY,
                    DOWNLOAD_ENTITY_4_QUALITY,
                    DOWNLOAD_ENTITY_4_TITLE,
                    DOWNLOAD_ENTITY_4_AUTHOR,
                    DOWNLOAD_ENTITY_4_THUMBNAIL_URL,
                    DOWNLOAD_ENTITY_4_REQUESTED_TIMESTAMP,
                    DOWNLOAD_ENTITY_4_DOWNLOAD_PATH,
                    DOWNLOAD_ENTITY_4_STATUS,
                    DOWNLOAD_ENTITY_4_SECONDS_LONG
                )
            )
        }
    }
}