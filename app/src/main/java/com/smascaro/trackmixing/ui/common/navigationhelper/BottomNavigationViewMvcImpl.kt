package com.smascaro.trackmixing.ui.common.navigationhelper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

//TODO remove, unused
class BottomNavigationViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<BottomNavigationViewMvc.Listener>(),
    BottomNavigationViewMvc {

    init {
        setRootView(inflater.inflate(R.layout.activity_main, parent, false))
            true

    }
}