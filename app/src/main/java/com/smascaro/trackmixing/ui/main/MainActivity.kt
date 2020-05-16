package com.smascaro.trackmixing.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseActivity
import com.smascaro.trackmixing.ui.common.navigationhelper.BottomNavigationViewMvc
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper

class MainActivity : BaseActivity(), BottomNavigationViewMvc.Listener {

    private lateinit var mNavigationHelper: NavigationHelper
    private lateinit var mViewMvc: BottomNavigationViewMvc
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.layout_main)
        mViewMvc = getCompositionRoot().getViewMvcFactory().getBottomNavigationViewMvc(null)
        mNavigationHelper = getCompositionRoot().getNavigationHelper()

//        val navView: BottomNavigationView = findViewById(R.id.nav_view)
//


        setContentView(mViewMvc.getRootView())
        val navController = findNavController(R.id.nav_host_fragment)
        mNavigationHelper.bindNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        mViewMvc.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        mViewMvc.unregisterListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem) {
        mNavigationHelper.navigateTo(item.itemId)
    }
}
