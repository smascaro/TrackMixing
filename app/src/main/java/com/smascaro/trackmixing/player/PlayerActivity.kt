package com.smascaro.trackmixing.player

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.events.ApplicationEvent
import com.smascaro.trackmixing.base.ui.BaseActivity
import com.smascaro.trackmixing.player.controller.TrackPlayerController
import com.smascaro.trackmixing.player.view.TrackPlayerViewMvcImpl_Activity
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class PlayerActivity : BaseActivity() {
    @Inject
    lateinit var trackPlayerController: TrackPlayerController

    @Inject
    lateinit var trackPlayerViewMvc: TrackPlayerViewMvcImpl_Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onCreate(savedInstanceState)
        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_player, null, false)

        trackPlayerViewMvc.bindRootView(rootView)
        trackPlayerController.bindViewMvc(trackPlayerViewMvc)
        trackPlayerController.handleIntent(intent)

        trackPlayerController.onCreate()
        setContentView(rootView)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().post(ApplicationEvent(ApplicationEvent.AppState.Foreground))
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().post(ApplicationEvent(ApplicationEvent.AppState.Background))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeControllers()
    }

    private fun disposeControllers() {
        trackPlayerController.dispose()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        trackPlayerController.handleIntent(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_out_top)
    }
}