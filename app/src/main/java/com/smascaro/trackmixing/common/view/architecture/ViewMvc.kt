package com.smascaro.trackmixing.common.view.architecture

import android.view.View

interface ViewMvc {
    fun getRootView(): View
    fun bindRootView(rootView: View?)
}