package com.smascaro.trackmixing.playbackservice.utils

import android.content.Context
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.common.utils.TrackVolumeBundle
import com.smascaro.trackmixing.common.view.architecture.BaseObservable
import com.smascaro.trackmixing.playbackservice.model.MixPlaybackState
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import timber.log.Timber
import javax.inject.Inject

class PlaybackHelper @Inject constructor(private val context: Context) :
    BaseObservable<PlaybackHelper.Listener>(),
    PlayingTrackState.Listener {
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

    private var mCurrentState: State =
        State.PAUSED
    private val playerRack = PlayerRack()
    fun isPlaying(): Boolean = mCurrentState == State.PLAYING

    fun getPlaybackState(): MixPlaybackState {
        return MixPlaybackState().apply {
            trackTitle = getTrack().title
            trackThumbnailUrl = getTrack().thumbnailUrl
            isMasterPlaying = isPlaying()
            isVocalsPlaying = true
            isOtherPlaying = true
            isBassPlaying = true
            isDrumsPlaying = true
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

        val mVocalsState = PlayingTrackState.create(
            track,
            TrackInstrument.VOCALS,
            context
        ).apply {
            registerListener(this@PlaybackHelper)
        }
        playerRack.put(mVocalsState.instrument, mVocalsState)
        val mOtherState = PlayingTrackState.create(
            track,
            TrackInstrument.OTHER, context
        ).apply {
            registerListener(this@PlaybackHelper)
        }
        playerRack.put(mOtherState.instrument, mOtherState)

        val mBassState = PlayingTrackState.create(
            track,
            TrackInstrument.BASS,
            context
        ).apply {
            registerListener(this@PlaybackHelper)
        }
        playerRack.put(mBassState.instrument, mBassState)

        val mDrumsState = PlayingTrackState.create(
            track,
            TrackInstrument.DRUMS,
            context
        ).apply {
            registerListener(this@PlaybackHelper)
        }
        playerRack.put(mDrumsState.instrument, mDrumsState)

        mIsInitialized = true
        getListeners().forEach {
            it.onInitializationFinished()
        }

    }

    private fun resetPlayersIfInitialized() {
        if (mIsInitialized) {
            finalize()
        }
    }

    fun finalize() {
        playerRack.unregisterListener(this)
        playerRack.finalize()
    }

    fun playMaster() {
        if (mCurrentState != State.PLAYING) {
            playerRack.play()
            mCurrentState =
                State.PLAYING
            notifyMediaStateChange()
        }
    }


    fun pauseMaster() {
        if (mCurrentState == State.PLAYING) {
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
        mCurrentState =
            State.PLAYING
        notifyMediaStateChange()
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
}