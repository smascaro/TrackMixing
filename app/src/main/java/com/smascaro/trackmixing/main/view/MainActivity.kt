package com.smascaro.trackmixing.main.view

import android.os.Bundle
import android.view.LayoutInflater
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.main.controller.BottomPlayerController
import com.smascaro.trackmixing.main.controller.MainActivityController
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainActivityController: MainActivityController

    @Inject
    lateinit var bottomPlayerController: BottomPlayerController

    @Inject
    lateinit var viewMvc: MainActivityViewMvc

    @Inject
    lateinit var bottomPlayerViewMvc: BottomPlayerViewMvc

    lateinit var mainComponent: MainComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)
        setContentView(rootView)

        mainComponent =
            (application as TrackMixingApplication).appComponent.mainComponent().create(this)
        mainComponent.inject(this)

        viewMvc.bindRootView(rootView)
        mainActivityController.bindViewMvc(viewMvc)
        mainActivityController.handleIntent(intent)

        bottomPlayerViewMvc.bindRootView(rootView)
        bottomPlayerController.bindViewMvc(bottomPlayerViewMvc)

        bottomPlayerController.onCreate()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().post(ApplicationEvent(AppState.Background()))
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomPlayerController.onDestroy()
    }
}
