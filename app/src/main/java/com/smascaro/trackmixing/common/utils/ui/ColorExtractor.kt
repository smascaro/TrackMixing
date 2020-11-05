package com.smascaro.trackmixing.common.utils.ui

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import com.bumptech.glide.RequestManager
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ColorExtractor @Inject constructor(
    private val glide: RequestManager
) {
    suspend fun extractLightVibrant(
        imageUrl: String,
    ): Int {
        val bitmap = glide.loadBitmap(imageUrl)
        return if ((bitmap != null)) {
            extractLightVibrant(bitmap)
        } else {
            Color.WHITE
        }
    }

    suspend fun extractLightVibrant(bitmap: Bitmap) = suspendCoroutine<Int> { cont ->
        Palette.Builder(bitmap).generate {
            val color = it?.getLightVibrantColor(Color.WHITE) ?: Color.WHITE
            cont.resume(color)
        }
    }
}