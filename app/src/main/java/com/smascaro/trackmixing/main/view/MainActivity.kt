package com.smascaro.trackmixing.main.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.main.components.bottomplayer.controller.BottomPlayerController
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.main.components.progress.controller.BottomProgressController
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.controller.MainActivityController
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent.AppState
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainActivityController: MainActivityController

    @Inject
    lateinit var mainViewMvc: MainActivityViewMvc

    @Inject
    lateinit var bottomPlayerController: BottomPlayerController

    @Inject
    lateinit var bottomPlayerViewMvc: BottomPlayerViewMvc

    @Inject
    lateinit var bottomProgressController: BottomProgressController

    @Inject
    lateinit var bottomProgressViewMvc: BottomProgressViewMvc

    lateinit var mainComponent: MainComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent =
            (application as TrackMixingApplication).appComponent.mainComponent().create(this)
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)

        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)


        mainViewMvc.bindRootView(rootView)
        mainActivityController.bindViewMvc(mainViewMvc)
        mainActivityController.handleIntent(intent)

        bottomPlayerViewMvc.bindRootView(rootView)
        bottomPlayerController.bindViewMvc(bottomPlayerViewMvc)

        bottomProgressViewMvc.bindRootView(rootView)
        bottomProgressController.bindViewMvc(bottomProgressViewMvc)

        bottomProgressController.onCreate()
        bottomPlayerController.onCreate()

        setContentView(rootView)
        bottomPlayerController.bindNavController(findNavController(R.id.nav_host_fragment))
    }

    override fun onStart() {
        super.onStart()
        bottomProgressController.onStart()
        EventBus.getDefault().post(ApplicationEvent(AppState.Foreground()))
    }

    override fun onStop() {
        super.onStop()
        bottomProgressController.onStop()
        EventBus.getDefault().post(ApplicationEvent(AppState.Background()))
    }
//
//    private var showAnim = true
//    override fun onBackPressed() {
//        if (showAnim) {
//            EventBus.getDefault().post(UiProgressEvent.ProgressUpdate(26, "Testing animation"))
//        } else {
//            EventBus.getDefault().post(UiProgressEvent.ProgressFinished())
//        }
//        showAnim = !showAnim
//    }

    override fun onDestroy() {
        super.onDestroy()
        bottomPlayerController.onDestroy()
    }
}
