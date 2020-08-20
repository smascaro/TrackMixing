package com.smascaro.trackmixing.common.utils

import android.content.Context
import android.util.TypedValue
import javax.inject.Inject

class UiUtils @Inject constructor(private val context: Context) {
    fun DpToPixels(dps: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps,
            context.resources.displayMetrics
        )
    }
}