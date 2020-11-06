package com.smascaro.trackmixing.base.ui.architecture.view

import android.view.View

interface ViewMvc {
    fun getRootView(): View
    fun bindRootView(rootView: View?)
}