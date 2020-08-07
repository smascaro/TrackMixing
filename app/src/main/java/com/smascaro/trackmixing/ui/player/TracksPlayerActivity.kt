package com.smascaro.trackmixing.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.navArgs
import com.smascaro.trackmixing.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_track_player.*

/**
 * TODO: Mvcify and design UI
 */
class TracksPlayerActivity : BaseActivity() {

    private lateinit var mTracksPlayerController: TracksPlayerController

    private val navigationArgs: TracksPlayerActivityArgs by navArgs()

    companion object {
        fun start(context: Context, filesBasePath: String) {
            val intent = Intent(context, TracksPlayerActivity::class.java)
            intent.putExtra("tracks_base_directory", filesBasePath)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tracksDir = navigationArgs.track
        val viewMvc = getCompositionRoot().getViewMvcFactory().getTracksPlayerViewMvc(null)
        mTracksPlayerController = getCompositionRoot().getTracksPlayerController(tracksDir)
        mTracksPlayerController.bindView(viewMvc)
        mTracksPlayerController.onCreate()

        setContentView(viewMvc.getRootView())
        btnPlayPauseMaster.setOnClickListener {
            mTracksPlayerController.playMaster()
        }
    }

    override fun onDestroy() {
        mTracksPlayerController.onDestroy()
        super.onDestroy()
    }
}
