package com.smascaro.trackmixing.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseActivity
import com.smascaro.trackmixing.ui.common.navigationhelper.BottomNavigationViewMvc
import com.smascaro.trackmixing.ui.common.navigationhelper.NavigationHelper
import timber.log.Timber

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

        if (intent.action == Intent.ACTION_SEND) {
            val url = if (intent.clipData != null && intent.clipData!!.itemCount > 0) {
                intent.clipData?.getItemAt(0)!!.text
            } else ""
            Toast.makeText(this, url, Toast.LENGTH_LONG).show()
            Timber.d(intent.toString())

        }
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
