package com.smascaro.trackmixing.common.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import javax.inject.Inject

class UiUtils @Inject constructor(private val context: Context) {
    fun DpToPixels(dps: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dps,
            context.resources.displayMetrics
        )
    }

    fun hideKeyboard(rootView: View) {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }
}