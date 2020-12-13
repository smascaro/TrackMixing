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
import com.smascaro.trackmixing.di.MainComponentProvider
import com.smascaro.trackmixing.main.components.bottomplayer.controller.BottomPlayerController
import com.smascaro.trackmixing.main.components.bottomplayer.view.BottomPlayerViewMvc
import com.smascaro.trackmixing.main.controller.MainActivityController
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
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val mainComponent = (application as MainComponentProvider).provideMainComponent()
        mainComponent.inject(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onCreate(savedInstanceState)
        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)


        mainViewMvc.bindRootView(rootView)
        mainActivityController.bindViewMvc(mainViewMvc)

        bottomPlayerViewMvc.bindRootView(rootView)
        bottomPlayerController.bindViewMvc(bottomPlayerViewMvc)
        bottomPlayerController.handleIntent(intent)

        bottomPlayerController.onCreate()

        setContentView(rootView)
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id != R.id.destination_player) {
                bottomPlayerController.unblockPlayerAppearance()
                bottomPlayerController.checkIfPlayerShouldShow()
            } else {
                bottomPlayerController.blockPlayerAppearance()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainActivityController.onStart()
        bottomPlayerController.bindNavController(navController)
        EventBus.getDefault().post(ApplicationEvent(AppState.Foreground))
    }

    override fun onStop() {
        super.onStop()
        mainActivityController.onStop()
        EventBus.getDefault().post(ApplicationEvent(AppState.Background))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeControllers()
    }

    private fun disposeControllers() {
        mainActivityController.dispose()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        bottomPlayerController.handleIntent(intent)
    }
}
