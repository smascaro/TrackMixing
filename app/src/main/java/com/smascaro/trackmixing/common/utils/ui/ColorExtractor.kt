package com.smascaro.trackmixing.common.utils.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ColorExtractor @Inject constructor(private val glide: RequestManager) {
    suspend fun extractLightVibrant(
        imageUrl: String,
    ) = suspendCoroutine<Int> { cont ->
        glide
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Palette.Builder(resource).generate {
                        val color = it?.getLightVibrantColor(Color.WHITE) ?: Color.WHITE
                        cont.resume(color)
                    }
                }
            })
    }
}