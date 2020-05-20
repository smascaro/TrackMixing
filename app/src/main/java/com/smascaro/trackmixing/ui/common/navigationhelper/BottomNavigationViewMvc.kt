package com.smascaro.trackmixing.ui.common.navigationhelper

import android.view.MenuItem
import com.smascaro.trackmixing.ui.common.BaseObservable
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import com.smascaro.trackmixing.ui.common.ObservableViewMvc

//TODO remove, unused
interface BottomNavigationViewMvc : ObservableViewMvc<BottomNavigationViewMvc.Listener> {
    interface Listener {
        fun onNavigationItemSelected(item: MenuItem)
    }
}