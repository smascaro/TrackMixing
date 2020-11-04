package com.smascaro.trackmixing.playbackservice.utils

import android.content.Context
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.time.Milliseconds
import com.smascaro.trackmixing.common.utils.time.Seconds
import com.smascaro.trackmixing.common.utils.time.asMillis
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import timber.log.Timber
import kotlin.math.ln

class InstrumentPlayer @Deprecated(
    "Use the provided Builder to create an instance.",
    ReplaceWith("PlayingTrackState.Builder { }")
) constructor() : BaseObservable<InstrumentPlayer.Listener>(), Player.EventListener {
    interface Listener {
        fun onPlayerPrepared(instrument: TrackInstrument)
        fun onPlayerCompletion(instrument: TrackInstrument)
        fun onPlayerError(instrument: TrackInstrument, errorMessage: String)
    }

    private constructor(instrument: TrackInstrument, context: Context) : this() {
        this.instrument = instrument
        this.context = context
    }

    class Builder(buildBlock: Builder.() -> Unit) {
        private var track: Track? = null
        private var context: Context? = null
        private var instrument: TrackInstrument? = null

        init {
            buildBlock()
        }

        fun setInstrument(trackInstrument: TrackInstrument): Builder {
            this.instrument = trackInstrument
            return this
        }

        fun withContext(context: Context): Builder {
            this.context = context
            return this
        }

        fun setTrack(track: Track): Builder {
            this.track = track
            return this
        }

        fun build(): InstrumentPlayer {
            if (instrument == null || context == null || track == null) {
                throw IllegalArgumentException("Instrument, context and track must be set in order for build to succeed")
            }
            val filename = when (instrument!!) {
                TrackInstrument.VOCALS -> VOCALS_FILENAME
                TrackInstrument.OTHER -> OTHER_FILENAME
                TrackInstrument.BASS -> BASS_FILENAME
                TrackInstrument.DRUMS -> DRUMS_FILENAME
            }
            return InstrumentPlayer(instrument!!, context!!).apply {
                initialize("${track!!.downloadPath}/$filename")
            }
        }
    }

    companion object {
        private const val VOCALS_FILENAME = "vocals.mp3"
        private const val OTHER_FILENAME = "other.mp3"
        private const val BASS_FILENAME = "bass.mp3"
        private const val DRUMS_FILENAME = "drums.mp3"
        private const val PLAYER_MAX_VOLUME = 100f
    }

    lateinit var instrument: TrackInstrument
    private lateinit var context: Context
    private val maxVolume: Float = PLAYER_MAX_VOLUME
    private var mIsPrepared: Boolean = false
    private lateinit var player: ExoPlayer
    private var mVolume = maxVolume
    private var hasTrackCompletedPlaying: Boolean = false
    private lateinit var progressiveMediaSourceFactory: ProgressiveMediaSource.Factory
    fun isPlaying(): Boolean {
        return player.isPlaying
    }

    var readyToPlay: Boolean = mIsPrepared
        get() = mIsPrepared
        private set

    fun initialize(path: String) {
        hasTrackCompletedPlaying = false
        val renderersFactory = DefaultRenderersFactory(context)
        player = SimpleExoPlayer.Builder(context, renderersFactory).build()
        player.playWhenReady = false
        progressiveMediaSourceFactory =
            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context))
        val mediaSource = progressiveMediaSourceFactory.createMediaSource(MediaItem.fromUri(path))
        player.setMediaSource(mediaSource)
        player.prepare()
        player.addListener(this)
    }

    override fun onPlaybackStateChanged(state: Int) {
        logPlaybackStateChange(state)
        super.onPlaybackStateChanged(state)
        when (state) {
            SimpleExoPlayer.STATE_READY -> handleReadyState()
            SimpleExoPlayer.STATE_ENDED -> handleTrackCompletion()
        }
    }

    private fun logPlaybackStateChange(state: Int) {
        val stateStr = when (state) {
            SimpleExoPlayer.STATE_BUFFERING -> "SimpleExoPlayer.STATE_BUFFERING"
            SimpleExoPlayer.STATE_IDLE -> "SimpleExoPlayer.STATE_IDLE"
            SimpleExoPlayer.STATE_READY -> "SimpleExoPlayer.STATE_READY"
            SimpleExoPlayer.STATE_ENDED -> "SimpleExoPlayer.STATE_ENDED"
            else -> "Unknown state"
        }
        Timber.d("[$instrument] - State changed to $stateStr")
    }

    private fun handleTrackCompletion() {
        hasTrackCompletedPlaying = true
        getListeners().forEach {
            it.onPlayerCompletion(instrument)
        }
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        logIsLoadingChanged(isLoading)
    }

    private fun logIsLoadingChanged(loading: Boolean) {
        Timber.d("[$instrument] IsLoading changed to $loading")
    }

    private fun handleReadyState() {
        mIsPrepared = true
        getListeners().forEach {
            it.onPlayerPrepared(instrument)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        getListeners().forEach { it.onPlayerError(instrument, error.localizedMessage) }
    }

    fun setVolume(volume: Int) {
        mVolume = volume.toFloat()
        updateVolume()
    }

    fun play() {
        Timber.d("[$instrument] - PLAY")
        player.playbackLooper.thread.run {
            player.play()
        }
    }

    fun pause() {
        Timber.d("[$instrument] - PAUSE")
        player.playbackLooper.thread.run {
            player.pause()
        }
    }

    fun finalize() {
        mIsPrepared = false
        player.stop()
        player.release()
        player.removeListener(this)
    }

    fun isCompleted(): Boolean {
        return hasTrackCompletedPlaying
    }

    private fun updateVolume() {
        val mediaPlayerVolume = (1 - (ln(maxVolume - mVolume) / ln(maxVolume)))
        player.audioComponent?.volume = mediaPlayerVolume
    }

    fun getVolume(): Float {
        return mVolume
    }

    fun getTimestamp(): Milliseconds {
        return player.currentPosition.asMillis()
    }

    fun seek(timestampSeconds: Seconds) {
        seekMillis(timestampSeconds.millis())
    }

    fun seekMillis(timestampMillis: Milliseconds) {
        player.seekTo(timestampMillis.value)
    }

    fun getCurrentPosition(): Milliseconds {
        return player.currentPosition.asMillis()
    }
}