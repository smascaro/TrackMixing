package com.smascaro.trackmixing.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.events.ApplicationEvent
import com.smascaro.trackmixing.base.events.ApplicationEvent.AppState
import com.smascaro.trackmixing.base.ui.BaseActivity
import com.smascaro.trackmixing.main.components.progress.controller.BottomProgressController
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.controller.MainActivityController
import com.smascaro.trackmixing.player.controller.TrackPlayerController
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvc
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainActivityController: MainActivityController

    @Inject
    lateinit var mainViewMvc: MainActivityViewMvc

    @Inject
    lateinit var trackPlayerController: TrackPlayerController

    @Inject
    lateinit var trackPlayerViewMvc: TrackPlayerViewMvc

    @Inject
    lateinit var bottomProgressController: BottomProgressController

    @Inject
    lateinit var bottomProgressViewMvc: BottomProgressViewMvc
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onCreate(savedInstanceState)
        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)


        mainViewMvc.bindRootView(rootView)
        mainActivityController.bindViewMvc(mainViewMvc)

        trackPlayerViewMvc.bindRootView(rootView)
        trackPlayerController.bindViewMvc(trackPlayerViewMvc)
        trackPlayerController.handleIntent(intent)

        bottomProgressViewMvc.bindRootView(rootView)
        bottomProgressController.bindViewMvc(bottomProgressViewMvc)


        bottomProgressController.onCreate()
        trackPlayerController.onCreate()

        setContentView(rootView)
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onStart() {
        super.onStart()
        mainActivityController.onStart()
        bottomProgressController.onStart()
        EventBus.getDefault().post(ApplicationEvent(AppState.Foreground))
    }

    override fun onStop() {
        super.onStop()
        mainActivityController.onStop()
        bottomProgressController.onStop()
        EventBus.getDefault().post(ApplicationEvent(AppState.Background))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeControllers()
    }

    private fun disposeControllers() {
        mainActivityController.dispose()
        bottomProgressController.dispose()
        trackPlayerController.dispose()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        trackPlayerController.handleIntent(intent)
    }

    override fun onBackPressed() {
        if (!trackPlayerViewMvc.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
