package com.smascaro.trackmixing.service

import com.smascaro.trackmixing.errorhandling.NoLoadedTrackException
import com.smascaro.trackmixing.tracks.Track
import com.smascaro.trackmixing.ui.common.BaseObservable
import timber.log.Timber

class PlaybackHelper : BaseObservable<PlaybackHelper.Listener>(), PlayingTrackState.Listener {
    enum class State {
        PLAYING, PAUSED
    }

    interface Listener {
        fun onInitializationFinished()
        fun onMediaStateChange()
        fun onSongFinished()
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

    fun getPlaybackState(): MixPlaybackState {
        return MixPlaybackState().apply {
            trackTitle = getTrack().title
            trackThumbnailUrl = getTrack().thumbnailUrl
            isMasterPlaying = isPlaying()
            isVocalsPlaying = isInstrumentPlaying(TrackInstrument.VOCALS)
            isOtherPlaying = isInstrumentPlaying(TrackInstrument.OTHER)
            isBassPlaying = isInstrumentPlaying(TrackInstrument.BASS)
            isDrumsPlaying = isInstrumentPlaying(TrackInstrument.DRUMS)
        }
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
        mCurrentState = State.PAUSED
        mCurrentPlayingTrack = track

        mVocalsState = PlayingTrackState.create(track, TrackInstrument.VOCALS).apply {
            registerListener(this@PlaybackHelper)
        }

        mOtherState = PlayingTrackState.create(track, TrackInstrument.OTHER).apply {
            registerListener(this@PlaybackHelper)
        }

        mBassState = PlayingTrackState.create(track, TrackInstrument.BASS).apply {
            registerListener(this@PlaybackHelper)
        }

        mDrumsState = PlayingTrackState.create(track, TrackInstrument.DRUMS).apply {
            registerListener(this@PlaybackHelper)
        }

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
        mVocalsState.apply {
            unregisterListener(this@PlaybackHelper)
            finalize()
        }
        mOtherState.apply {
            unregisterListener(this@PlaybackHelper)
            finalize()
        }
        mBassState.apply {
            unregisterListener(this@PlaybackHelper)
            finalize()
        }
        mDrumsState.apply {
            unregisterListener(this@PlaybackHelper)
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
                notifyMediaStateChange()
            }
        }
    }

    fun muteTrack(instrument: TrackInstrument) {
        when (instrument) {
            TrackInstrument.VOCALS -> mVocalsState.mute()
            TrackInstrument.OTHER -> mOtherState.mute()
            TrackInstrument.BASS -> mBassState.mute()
            TrackInstrument.DRUMS -> mDrumsState.mute()
        }
        notifyMediaStateChange()
    }

    fun unmuteTrack(instrument: TrackInstrument) {
        when (instrument) {
            TrackInstrument.VOCALS -> mVocalsState.unmute()
            TrackInstrument.OTHER -> mOtherState.unmute()
            TrackInstrument.BASS -> mBassState.unmute()
            TrackInstrument.DRUMS -> mDrumsState.unmute()
        }
        notifyMediaStateChange()
    }

    fun pauseMaster() {
        if (mCurrentState == State.PLAYING) {
            pauseAll()
            mCurrentState = State.PAUSED
            notifyMediaStateChange()
        }
    }

    private fun notifyMediaStateChange() {
        getListeners().forEach {
            it.onMediaStateChange()
        }
    }

    fun isInstrumentPlaying(instrument: TrackInstrument): Boolean {
        return when (instrument) {
            TrackInstrument.VOCALS -> mVocalsState.isPlaying()
            TrackInstrument.OTHER -> mOtherState.isPlaying()
            TrackInstrument.BASS -> mBassState.isPlaying()
            TrackInstrument.DRUMS -> mDrumsState.isPlaying()
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
                mCurrentState = State.PLAYING
                mPlayRequested = false
            }
        }
    }

    override fun onPlayerCompletion(instrument: TrackInstrument) {
        Timber.i("Track $instrument has completed")
        mCurrentState = State.PAUSED
        notifyMediaStateChange()
        getListeners().forEach {
            it.onSongFinished()
        }
    }

    override fun onPlayerError(instrument: TrackInstrument, errorMessage: String) {
        Timber.e("Error on track $instrument: $errorMessage")
    }
}