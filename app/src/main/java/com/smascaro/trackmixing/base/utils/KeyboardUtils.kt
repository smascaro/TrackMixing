package com.smascaro.trackmixing.base.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

object KeyboardUtils {

    fun show(binding: ViewBinding) = show(binding.root)

    fun show(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hide(binding: ViewBinding) = hide(binding.root)

    fun hide(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}