package com.smascaro.trackmixing.common.utils.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun RequestManager.loadBitmap(imageUrl: String) = suspendCoroutine<Bitmap?> { cont ->
    this.asBitmap()
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                // Nothing to do
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                cont.resume(null)
            }

            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                cont.resume(resource)
            }
        })
}