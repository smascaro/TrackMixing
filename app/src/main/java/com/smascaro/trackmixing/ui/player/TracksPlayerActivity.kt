package com.smascaro.trackmixing.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.navArgs
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.TrackMixingApplication
import com.smascaro.trackmixing.common.di.player.PlayerComponent
import com.smascaro.trackmixing.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_track_player.*
import javax.inject.Inject

/**
 * TODO: Mvcify and design UI
 */
class TracksPlayerActivity : BaseActivity() {

    @Inject
    lateinit var viewMvc: TracksPlayerViewMvc

    @Inject
    lateinit var mTracksPlayerController: TracksPlayerController

    private val navigationArgs: TracksPlayerActivityArgs by navArgs()

    private lateinit var playerComponent: PlayerComponent

    companion object {
        fun start(context: Context, filesBasePath: String) {
            val intent = Intent(context, TracksPlayerActivity::class.java)
            intent.putExtra("tracks_base_directory", filesBasePath)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        playerComponent =
            (application as TrackMixingApplication).appComponent.playerComponent().create()
        playerComponent.inject(this)
        super.onCreate(savedInstanceState)
        val track = navigationArgs.track

        val rootView =
            LayoutInflater.from(this).inflate(R.layout.activity_track_player, null, false)
        viewMvc.bindRootView(rootView)

        mTracksPlayerController.bindTrack(track)
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
