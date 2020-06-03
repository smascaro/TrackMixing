package com.smascaro.trackmixing.service

import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservable
import timber.log.Timber

class PlayingHelper : BaseObservable<PlayingHelper.Listener>(), PlayingTrackState.Listener {
    enum class State {
        PLAYING, PAUSED
    }

    interface Listener {
        fun onInitializtionFinished()
        fun onMediaStateChange()
    }

    private lateinit var mVocalsState: PlayingTrackState
    private lateinit var mOtherState: PlayingTrackState
    private lateinit var mBassState: PlayingTrackState
    private lateinit var mDrumsState: PlayingTrackState

    private var mPlayRequested: Boolean = false
    private lateinit var mCurrentPlayingTrack: Track
    private var mBasePath: String = ""
        get() = mCurrentPlayingTrack.downloadPath
    private var mIsInitialized: Boolean = false

    private var mCurrentState: State = State.PAUSED
    fun isPlaying(): Boolean = mCurrentState == State.PLAYING
    fun getTrack(): Track {
        return mCurrentPlayingTrack
    }

    fun initialize(track: Track) {
        resetPlayersIfInitialized()
        mCurrentPlayingTrack = track
        mVocalsState = PlayingTrackState(TrackInstrument.VOCALS).apply {
            registerListener(this@PlayingHelper)
            initialize("$mBasePath/vocals.mp3")
        }

        mOtherState = PlayingTrackState(TrackInstrument.OTHER).apply {
            registerListener(this@PlayingHelper)
            initialize("$mBasePath/other.mp3")
        }

        mBassState = PlayingTrackState(TrackInstrument.BASS).apply {
            registerListener(this@PlayingHelper)
            initialize("$mBasePath/bass.mp3")
        }

        mDrumsState = PlayingTrackState(TrackInstrument.DRUMS).apply {
            registerListener(this@PlayingHelper)
            initialize("$mBasePath/drums.mp3")
        }
        mIsInitialized = true
        getListeners().forEach {
            it.onInitializtionFinished()
        }
    }

    private fun resetPlayersIfInitialized() {
        if (mIsInitialized) {
            finalize()
        }
    }

    fun finalize() {
        mVocalsState.apply {
            unregisterListener(this@PlayingHelper)
            finalize()
        }
        mOtherState.apply {
            unregisterListener(this@PlayingHelper)
            finalize()
        }
        mBassState.apply {
            unregisterListener(this@PlayingHelper)
            finalize()
        }
        mDrumsState.apply {
            unregisterListener(this@PlayingHelper)
            finalize()
        }
    }

    private fun isAllReady(): Boolean {
        return mVocalsState.readyToPlay &&
                mOtherState.readyToPlay &&
                mBassState.readyToPlay &&
                mDrumsState.readyToPlay
    }

    fun playMaster() {
        if (mCurrentState != State.PLAYING) {
            if (!isAllReady()) {
                mPlayRequested = true
            } else {
                playAll()
                mCurrentState = State.PLAYING
                getListeners().forEach { it.onMediaStateChange() }
            }
        }
    }

    fun pauseMaster() {
        if (mCurrentState == State.PLAYING) {
            pauseAll()
            mCurrentState = State.PAUSED
            getListeners().forEach { it.onMediaStateChange() }
        }
    }

    private fun playAll() {
        mVocalsState.play()
        mOtherState.play()
        mBassState.play()
        mDrumsState.play()
    }

    private fun pauseAll() {
        mVocalsState.pause()
        mOtherState.pause()
        mBassState.pause()
        mDrumsState.pause()
    }

    override fun onPlayerPrepared(instrument: TrackInstrument) {
        //If playMaster has been requested while players were not loaded, a request will be kept until all players are prepared and ready
        if (mPlayRequested) {
            if (isAllReady()) {
                playAll()
                mPlayRequested = false
            }
        }
    }

    override fun onPlayerCompletion(instrument: TrackInstrument) {
        Timber.i("Track $instrument has completed")
    }

    override fun onPlayerError(instrument: TrackInstrument, errorMessage: String) {
        Timber.e("Error on track $instrument: $errorMessage")
    }
}