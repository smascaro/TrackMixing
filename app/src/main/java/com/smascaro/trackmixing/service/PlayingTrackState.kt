package com.smascaro.trackmixing.service

import android.media.MediaPlayer
import com.smascaro.trackmixing.ui.common.BaseObservable
import java.io.File
import java.io.FileInputStream

class PlayingTrackState(
    val instrument: TrackInstrument
) : BaseObservable<PlayingTrackState.Listener>(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    interface Listener {
        fun onPlayerPrepared(instrument: TrackInstrument)
        fun onPlayerCompletion(instrument: TrackInstrument)
        fun onPlayerError(instrument: TrackInstrument, errorMessage: String)
    }

    private var mIsPrepared: Boolean = false
    private lateinit var mPlayer: MediaPlayer
    private var mVolume = 1.0f
    var readyToPlay: Boolean = mIsPrepared
        get() = mIsPrepared
        private set

    fun initialize(path: String) {
        val fileStream = FileInputStream(File(path))
        mPlayer = MediaPlayer()
        mPlayer.setOnPreparedListener(this)
        mPlayer.setOnCompletionListener(this)
        mPlayer.setOnErrorListener(this)
        mPlayer.setDataSource(fileStream.fd)
        mPlayer.prepareAsync()
    }

    fun incrementVolume() {
        mVolume += 0.01f
        mVolume.coerceAtMost(1.0f)
        updateVolume()
    }

    fun decrementVolume() {
        mVolume -= 0.01f
        mVolume.coerceAtLeast(0.0f)
        updateVolume()
    }

    fun mute() {
        mVolume = 0.0f
        updateVolume()
    }

    fun unmute() {
        mVolume = 1.0f
        updateVolume()
    }

    /**
     * To be called after initialize or an error will happen
     */
    fun play() {
        if (mIsPrepared && !mPlayer.isPlaying) {
            mPlayer.start()
        }
    }

    fun pause() {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
        }
    }

    fun finalize() {
        mPlayer.release()
        mIsPrepared = false
    }

    private fun updateVolume() {
        mPlayer.setVolume(mVolume, mVolume)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mIsPrepared = true
        getListeners().forEach {
            it.onPlayerPrepared(instrument)
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        getListeners().forEach {
            it.onPlayerCompletion(instrument)
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mIsPrepared = false
        val whatMsg = when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown error"
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Server died"
            else -> "Unrecognized error code"
        }
        val extraMsg = when (extra) {
            MediaPlayer.MEDIA_ERROR_IO -> "I/O Error"
            MediaPlayer.MEDIA_ERROR_MALFORMED -> "Malformed data"
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Unsupported format"
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "Timed out"
            else -> "Unrecognized extra error code"
        }
        getListeners().forEach {
            it.onPlayerError(instrument, "$whatMsg - $extraMsg")
        }
        return true
    }
}