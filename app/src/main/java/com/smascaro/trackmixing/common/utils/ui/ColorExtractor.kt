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

class ColorExtractor @Inject constructor(private val glide: RequestManager) {
    fun extractLightVibrant(imageUrl: String, then: (color: Int) -> Unit) {
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
                        then((color))
                    }
                }
            })
    }
}