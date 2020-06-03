package com.smascaro.trackmixing.ui.player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.navArgs
import com.smascaro.trackmixing.ui.common.BaseActivity
import kotlinx.android.synthetic.main.activity_track_player.*

/**
 * TODO: Mvcify and design UI
 */
class TracksPlayerActivity : BaseActivity() {

    private lateinit var mTracksPlayerController: TracksPlayerController
    private var mVocalsVolume = 1.0f
    private var mOtherVolume = 1.0f
    private var mBassVolume = 1.0f
    private var mDrumsVolume = 1.0f
    private var mVocalsPlayer = MediaPlayer()
    private var mOtherPlayer = MediaPlayer()
    private var mBassPlayer = MediaPlayer()
    private var mDrumsPlayer = MediaPlayer()

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
//        setContentView(R.layout.activity_track_player)

        val tracksDir = navigationArgs.track
        val viewMvc = getCompositionRoot().getViewMvcFactory().getTracksPlayerViewMvc(null)
        mTracksPlayerController = getCompositionRoot().getTracksPlayerController(tracksDir)
        mTracksPlayerController.bindView(viewMvc)
        mTracksPlayerController.startService()
        setContentView(viewMvc.getRootView())
//        if (tracksDir.isNotEmpty()) {
//            val fisVocals = FileInputStream(File(tracksDir, "vocals.mp3"))
//            mVocalsPlayer.setDataSource(fisVocals.fd)
//            mVocalsPlayer.prepare()
//            val fisOther = FileInputStream(File(tracksDir, "other.mp3"))
//            mOtherPlayer.setDataSource(fisOther.fd)
//            mOtherPlayer.prepare()
//            val fisBass = FileInputStream(File(tracksDir, "bass.mp3"))
//            mBassPlayer.setDataSource(fisBass.fd)
//            mBassPlayer.prepare()
//            val fisDrums = FileInputStream(File(tracksDir, "drums.mp3"))
//            mDrumsPlayer.setDataSource(fisDrums.fd)
//            mDrumsPlayer.prepare()
//
//        }
        btnPlayPauseMaster.setOnClickListener {
            mTracksPlayerController.playMaster()
        }

        btnPlayPauseOther.setOnClickListener {

            Toast.makeText(this, "Play/Pause Other track", Toast.LENGTH_SHORT).show()
        }
        btnVolDownOther.setOnClickListener {
            Toast.makeText(this, "Volume down other track", Toast.LENGTH_SHORT).show()
        }
        btnVolUpOther.setOnClickListener {
            Toast.makeText(this, "Volume up other track", Toast.LENGTH_SHORT).show()
        }
        btnPlayPauseVocals.setOnClickListener {
            Toast.makeText(this, "Play/Pause vocals track", Toast.LENGTH_SHORT).show()
        }
        btnVolDownVocals.setOnClickListener {
            Toast.makeText(this, "Volume down vocals track", Toast.LENGTH_SHORT).show()
        }
        btnVolUpVocals.setOnClickListener {
            Toast.makeText(this, "Volume up vocals track", Toast.LENGTH_SHORT).show()
        }

        btnPlayPauseBass.setOnClickListener {
            Toast.makeText(this, "Play/Pause bass track", Toast.LENGTH_SHORT).show()
        }
        btnVolDownBass.setOnClickListener {
            Toast.makeText(this, "Volume down bass track", Toast.LENGTH_SHORT).show()
        }
        btnVolUpBass.setOnClickListener {
            Toast.makeText(this, "Volume up bass track", Toast.LENGTH_SHORT).show()
        }

        btnPlayPauseDrums.setOnClickListener {
            Toast.makeText(this, "Play/Pause drums track", Toast.LENGTH_SHORT).show()
        }
        btnVolDownDrums.setOnClickListener {
            Toast.makeText(this, "Volume down drums track", Toast.LENGTH_SHORT).show()
        }
        btnVolUpDrums.setOnClickListener {
            Toast.makeText(this, "Volume up drums track", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        mTracksPlayerController.onDestroy()
        super.onDestroy()
    }
}
