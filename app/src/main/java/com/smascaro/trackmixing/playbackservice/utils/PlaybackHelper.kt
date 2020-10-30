package com.smascaro.trackmixing.playbackservice.utils

import android.content.Context
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.di.coroutines.MainCoroutineScope
import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.MixPlaybackState
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs

class PlaybackHelper @Inject constructor(
    private val context: Context,
    private val audioStateManager: AudioStateManager,
    uiScope: MainCoroutineScope
) :
    BaseObservable<PlaybackHelper.Listener>(),
    PlayingTrackState.Listener,
    AudioStateManager.Listener {
    enum class State {
        PLAYING, PAUSED
    }

    interface Listener {
        fun onInitializationFinished()
        fun onMediaStateChange()
        fun onSongFinished()
    }

    private lateinit var mCurrentPlayingTrack: Track
    private var mIsInitialized: Boolean = false
    private var playRequested: Boolean = false
    private var mCurrentState: State =
        State.PAUSED
    private val playerRack = PlayerRack(uiScope)
    fun isPlaying(): Boolean = mCurrentState == State.PLAYING

    fun getPlaybackState(): MixPlaybackState {
        return MixPlaybackState().apply {
            trackTitle = getTrack().title
            author = getTrack().author
            trackThumbnailUrl = getTrack().thumbnailUrl
            isMasterPlaying = isPlaying()
        }
    }

    fun isInitialized(): Boolean {
        return mIsInitialized
    }

    fun getTrack(): Track {
        if (!this::mCurrentPlayingTrack.isInitialized) {
            throw NoLoadedTrackException()
        }
        return mCurrentPlayingTrack
    }

    fun initialize(track: Track) {
        resetPlayersIfInitialized()
        mIsInitialized = false
        mCurrentState =
            State.PAUSED
        mCurrentPlayingTrack = track
        audioStateManager.registerListener(this)

        initializeTrackPlayingState(TrackInstrument.VOCALS, track)
        initializeTrackPlayingState(TrackInstrument.OTHER, track)
        initializeTrackPlayingState(TrackInstrument.BASS, track)
        initializeTrackPlayingState(TrackInstrument.DRUMS, track)

        mIsInitialized = true
        getListeners().forEach {
            it.onInitializationFinished()
        }
    }

    private fun initializeTrackPlayingState(trackInstrument: TrackInstrument, track: Track) {
        val playingState = PlayingTrackState.Builder {
            withContext(context)
            setInstrument(trackInstrument)
            setTrack(track)
        }.build()
        playingState.registerListener(this)
        playerRack.put(playingState.instrument, playingState)
    }

    private fun resetPlayersIfInitialized() {
        if (mIsInitialized) {
            finalize()
        }
    }

    fun finalize() {
        playerRack.unregisterListener(this)
        playerRack.finalize()
        audioStateManager.unregisterListener(this)
    }

    fun playMaster() {
        val isReady = playerRack.isReadyToPlay()
        if (mCurrentState != State.PLAYING && isReady) {
            if (audioStateManager.requestAudioFocus()) {
                Timber.d("Audio focus request granted")
                playerRack.play()
                mCurrentState =
                    State.PLAYING
                notifyMediaStateChange()
            } else {
                Timber.w("Audio focus request revoked")
            }
        } else if (!isReady) {
            playRequested = true
        }
    }

    fun pauseMaster() {
        if (mCurrentState == State.PLAYING) {
            audioStateManager.abandonAudioFocus()
            playerRack.pause()
            mCurrentState =
                State.PAUSED
            notifyMediaStateChange()
        }
    }

    fun seekMaster(newTimestampSeconds: Int) {
        playerRack.seek(newTimestampSeconds)
    }

    private fun notifyMediaStateChange() {
        getListeners().forEach {
            it.onMediaStateChange()
        }
    }

    override fun onPlayerPrepared(instrument: TrackInstrument) {
        Timber.d("[$instrument] is now prepared")
        if (playerRack.isReadyToPlay()) {
            Timber.d("All tracks are ready")
            playMaster()
            playRequested = false
            mCurrentState =
                State.PLAYING
            notifyMediaStateChange()
        } else {
            Timber.d("But not all tracks are ready")
        }
    }

    override fun onPlayerCompletion(instrument: TrackInstrument) {
        Timber.i("Track $instrument has completed")
        if (allTracksCompleted()) {
            mCurrentState =
                State.PAUSED
            notifyMediaStateChange()
            getListeners().forEach {
                it.onSongFinished()
            }
        }
    }

    private fun allTracksCompleted(): Boolean {
        return playerRack.allCompleted()
    }

    override fun onPlayerError(instrument: TrackInstrument, errorMessage: String) {
        Timber.e("Error on track $instrument: $errorMessage")
    }

    fun setMasterVolume(volume: Int) {
        playerRack.setVolume(volume)
    }

    fun setVolume(trackInstrument: TrackInstrument, volume: Int) {
        playerRack.setVolume(volume, trackInstrument)
    }

    fun getVolumesBundle(): TrackVolumeBundle {
        return playerRack.getVolumesBundle()
    }

    fun getTimestampSeconds(): Int {
        return (playerRack.getCurrentPosition()).toInt()
    }

    override fun onAudioFocusLoss() {
        pauseMaster()
    }

    override fun onAudioFocusTransientLoss() {
        pauseMaster()
    }

    suspend fun reportPlayersOffsets() {
        playerRack.getCurrentPositionsReport { report ->
            val sum = report.sumBy { it.second.toInt() }
            val mean = sum.toDouble() / report.size
            Timber.i("------------------ BEGIN REPORT TRACKS OFFSET ANALYSIS ---------------------")
            report.forEach {
                Timber.i(
                    "[${
                        it.first.name.padEnd(
                            16,
                            ' '
                        )
                    }] at position ${it.second} with offset of ${it.second - mean}"
                )
            }
            val maxDifference = findMaxDifference(report.map { it.second.toInt() })
            Timber.i("Max difference found is of $maxDifference milliseconds")
            Timber.i("------------------ END REPORT TRACKS OFFSET ANALYSIS ---------------------")
            if (maxDifference > 30) {
//                playerRack.seekMillis(mean.toLong())
            }
        }
    }

    private fun findMaxDifference(items: List<Int>): Int {
        var maxDiff = 0
        for (i in items.indices) {
            for (j in i until items.size) {
                maxDiff = maxOf(abs(items[i] - items[j]), maxDiff)
            }
        }
        return maxDiff
    }
}