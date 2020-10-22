package com.smascaro.trackmixing.common.utils.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import javax.inject.Inject

class UiUtils @Inject constructor(private val context: Context) {
    fun hideKeyboard(rootView: View) {
        val imm =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }
}