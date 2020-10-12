package com.smascaro.trackmixing.main.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.TrackMixingApplication
import com.smascaro.trackmixing.common.di.main.MainComponent
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.common.view.ui.BaseFragment
import com.smascaro.trackmixing.main.components.player.controller.TrackPlayerController
import com.smascaro.trackmixing.main.components.player.view.TrackPlayerViewMvc
import com.smascaro.trackmixing.main.components.progress.controller.BottomProgressController
import com.smascaro.trackmixing.main.components.progress.view.BottomProgressViewMvc
import com.smascaro.trackmixing.main.components.toolbar.controller.ToolbarController
import com.smascaro.trackmixing.main.components.toolbar.view.ToolbarViewMvc
import com.smascaro.trackmixing.main.controller.MainActivityController
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent
import com.smascaro.trackmixing.player.business.downloadtrack.model.ApplicationEvent.AppState
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : BaseActivity(), BaseFragment.OnTitleChangeListener {
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

    @Inject
    lateinit var toolbarController: ToolbarController

    @Inject
    lateinit var toolbarViewMvc: ToolbarViewMvc

    lateinit var mainComponent: MainComponent
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent =
            (application as TrackMixingApplication).appComponent.mainComponent().create(this)
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)

        val rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)


        mainViewMvc.bindRootView(rootView)
        mainActivityController.bindViewMvc(mainViewMvc)
        mainActivityController.handleIntent(intent)

        trackPlayerViewMvc.bindRootView(rootView)
        trackPlayerController.bindViewMvc(trackPlayerViewMvc)
        trackPlayerController.handleIntent(intent)

        bottomProgressViewMvc.bindRootView(rootView)
        bottomProgressController.bindViewMvc(bottomProgressViewMvc)

        toolbarViewMvc.bindRootView(rootView)
        toolbarViewMvc.bindActivity(this)
        toolbarController.bindViewMvc(toolbarViewMvc)

        bottomProgressController.onCreate()
        trackPlayerController.onCreate()

        setContentView(rootView)
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onStart() {
        super.onStart()
        trackPlayerController.bindNavController(navController)
        toolbarController.bindNavController(navController)
        mainActivityController.onStart()
        bottomProgressController.onStart()
        toolbarController.onStart()
        EventBus.getDefault().post(ApplicationEvent(AppState.Foreground()))
    }

    override fun onStop() {
        super.onStop()
        mainActivityController.onStop()
        bottomProgressController.onStop()
        toolbarController.onStop()
        EventBus.getDefault().post(ApplicationEvent(AppState.Background()))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeControllers()
    }

    private fun disposeControllers() {
        mainActivityController.dispose()
        bottomProgressController.dispose()
        trackPlayerController.dispose()
        toolbarController.dispose()
    }

    override fun changeTitle(title: String, enableBackNavigation: Boolean) {
        toolbarController.updateTitle(title, enableBackNavigation)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        trackPlayerController.handleIntent(intent)
    }

    override fun onBackPressed() {
        if(!trackPlayerViewMvc.onBackPressed()){
            super.onBackPressed()
        }
    }
}
