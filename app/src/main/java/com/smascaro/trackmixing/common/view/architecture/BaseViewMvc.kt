package com.smascaro.trackmixing.common.view.architecture

import android.content.Context
import android.view.View

abstract class BaseViewMvc :
    ViewMvc {
    private lateinit var mRootView: View

    override fun getRootView(): View {
        return mRootView
    }

    override fun bindRootView(rootView: View?) {
        if (rootView != null) {
            mRootView = rootView
        }
    }

    protected fun getContext(): Context? {
        return getRootView().context
    }

    protected fun <T : View> findViewById(id: Int): T {
        return getRootView().findViewById(id)
    }
}