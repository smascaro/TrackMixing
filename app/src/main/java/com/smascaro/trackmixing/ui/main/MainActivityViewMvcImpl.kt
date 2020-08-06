package com.smascaro.trackmixing.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

class MainActivityViewMvcImpl(
    inflater: LayoutInflater,
    val parent: ViewGroup?
) : MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>() {
    init {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false))
    }

    override fun displayRequestError(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }
}