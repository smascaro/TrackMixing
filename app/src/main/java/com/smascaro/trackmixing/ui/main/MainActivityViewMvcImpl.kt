package com.smascaro.trackmixing.ui.main

import android.widget.Toast
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import javax.inject.Inject

class MainActivityViewMvcImpl @Inject constructor() :
    MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>() {

    override fun displayRequestError(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }
}