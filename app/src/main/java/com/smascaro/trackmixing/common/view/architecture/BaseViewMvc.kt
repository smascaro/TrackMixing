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
        initialize()
        initializeListeners()
    }

    /**
     * Here is where all Ui elements are initialized. Typically <i>findViewById</i> calls will be made in this method.
     */
    open fun initialize() {}

    /**
     * This method will be called after  initialize() method, so any View initialized there, here will be ready to set listeners.
     */
    open fun initializeListeners() {}

    protected fun getContext(): Context? {
        return getRootView().context
    }

    protected fun <T : View> findViewById(id: Int): T {
        return getRootView().findViewById(id)
    }
}