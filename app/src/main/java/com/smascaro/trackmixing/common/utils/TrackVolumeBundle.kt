package com.smascaro.trackmixing.common.utils

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class TrackVolumeBundle(
    @SerializedName("VOCALS")
    val vocals: Int,
    @SerializedName("OTHER")
    val other: Int,
    @SerializedName("BASS")
    val bass: Int,
    @SerializedName("DRUMS")
    val drums: Int
) {
    fun bundleAsString(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun parse(bundle: String): TrackVolumeBundle {
            return Gson().fromJson(bundle, TrackVolumeBundle::class.java)
        }

        fun getDefault(): TrackVolumeBundle {
            return TrackVolumeBundle(100, 100, 100, 100)
        }
    }
}