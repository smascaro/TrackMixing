package com.smascaro.trackmixing.playbackservice.utils

import android.media.MediaPlayer
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.MEDIA_PLAYER_MAX_VOLUME
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import java.io.File
import java.io.FileInputStream
import kotlin.math.ln

class PlayingTrackState(
    val instrument: TrackInstrument
) : BaseObservable<PlayingTrackState.Listener>(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    interface Listener {
        fun onPlayerPrepared(instrument: TrackInstrument)
        fun onPlayerCompletion(instrument: TrackInstrument)
        fun onPlayerError(instrument: TrackInstrument, errorMessage: String)
    }

    companion object {
        private val VOCALS_FILENAME = "vocals.mp3"
        private val OTHER_FILENAME = "other.mp3"
        private val BASS_FILENAME = "bass.mp3"
        private val DRUMS_FILENAME = "drums.mp3"
        fun create(track: Track, instrument: TrackInstrument): PlayingTrackState {
            val filename = when (instrument) {
                TrackInstrument.VOCALS -> VOCALS_FILENAME
                TrackInstrument.OTHER -> OTHER_FILENAME
                TrackInstrument.BASS -> BASS_FILENAME
                TrackInstrument.DRUMS -> DRUMS_FILENAME
            }
            val playingTrackState = PlayingTrackState(
                instrument
            ).apply {
                initialize("${track.downloadPath}/$filename")
            }
            return playingTrackState
        }
    }

    enum class LogicMediaState {
        MUTED, PLAYING
    }

    val maxVolume: Float = MEDIA_PLAYER_MAX_VOLUME
    private var mCurrentMediaState: LogicMediaState =
        LogicMediaState.PLAYING
    private var mIsPrepared: Boolean = false
    private lateinit var mPlayer: MediaPlayer
    private var mVolume = maxVolume
    private var hasTrackCompletedPlaying: Boolean = false
    fun isPlaying(): Boolean {
        return mCurrentMediaState == LogicMediaState.PLAYING
    }

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
        hasTrackCompletedPlaying = false
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

    fun setVolume(volume: Int) {
        mVolume = volume.toFloat()
        updateVolume()
    }

    fun mute() {
        mVolume = 0.0f
        updateVolume()
        mCurrentMediaState =
            LogicMediaState.MUTED
    }

    fun unmute() {
        mVolume = 1.0f
        updateVolume()
        mCurrentMediaState =
            LogicMediaState.PLAYING
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

    fun isCompleted(): Boolean {
        return hasTrackCompletedPlaying
    }

    private fun updateVolume() {
        val mediaPlayerVolume = (1 - (ln(maxVolume - mVolume) / ln(maxVolume)))
        mPlayer.setVolume(mediaPlayerVolume, mediaPlayerVolume)
        if (mVolume == 0.0f) {
            mCurrentMediaState =
                LogicMediaState.MUTED
        } else if (mVolume > 0.0f && mCurrentMediaState == LogicMediaState.MUTED) {
            mCurrentMediaState =
                LogicMediaState.PLAYING
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mIsPrepared = true
        getListeners().forEach {
            it.onPlayerPrepared(instrument)
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        hasTrackCompletedPlaying = true
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

    fun getVolume(): Float {
        return mVolume
    }

    fun getTimestampMillis(): Int {
        return mPlayer.currentPosition
    }

    fun seek(timestampSeconds: Int) {
        mPlayer.seekTo(timestampSeconds * 1000)
    }
}