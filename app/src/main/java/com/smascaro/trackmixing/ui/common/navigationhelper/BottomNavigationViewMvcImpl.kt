package com.smascaro.trackmixing.ui.common.navigationhelper

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Switch
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc

class BottomNavigationViewMvcImpl(inflater: LayoutInflater, parent: ViewGroup?) :
    BaseObservableViewMvc<BottomNavigationViewMvc.Listener>(),
    BottomNavigationViewMvc {
    private val mBottomNavigationView: BottomNavigationView

    //TODO: no fer-ho view mvc sino helper
    init {
        setRootView(inflater.inflate(R.layout.layout_main, parent, false))
        mBottomNavigationView = findViewById(R.id.nav_view)
        mBottomNavigationView.setOnNavigationItemSelectedListener { item ->
            getListeners().forEach { listener ->
                listener.onNavigationItemSelected(item)
            }
            true
        }
    }
}