package com.smascaro.trackmixing.data

import android.content.Context
import com.smascaro.trackmixing.common.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING
import com.smascaro.trackmixing.common.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING
import javax.inject.Inject

class PlaybackStateManager @Inject constructor(context: Context) {
    private val sharedPreferences =
        SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(context)

    sealed class PlaybackState(protected val value: Int) {
        companion object {
            val PLAYBACK_STATE_PLAYING: Int = 0
            val PLAYBACK_STATE_PAUSED = 1
            val PLAYBACK_STATE_STOPPED = 2
            fun parse(stateValue: Int): PlaybackState? {
                return when (stateValue) {
                    PLAYBACK_STATE_PLAYING -> Playing()
                    PLAYBACK_STATE_PAUSED -> Paused()
                    PLAYBACK_STATE_STOPPED -> Stopped()
                    else -> null
                }
            }
        }

        fun getIntValue() = value
        class Playing : PlaybackState(PLAYBACK_STATE_PLAYING)
        class Paused : PlaybackState(PLAYBACK_STATE_PAUSED)
        class Stopped : PlaybackState(PLAYBACK_STATE_STOPPED)
    }

    fun setPlayingStateFlag(state: PlaybackState) {
        setPlayingStateFlag(state.getIntValue())
    }

    private fun setPlayingStateFlag(state: Int) {
        sharedPreferences
            .edit()
            .putInt(SHARED_PREFERENCES_PLAYBACK_IS_PLAYING, state)
            .apply()
    }

    fun setCurrentSongIdPlaying(videoId: String) {
        sharedPreferences
            .edit()
            .putString(SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING, videoId)
            .apply()
    }

    fun getPlayingState(): PlaybackState {
        val stateValue = sharedPreferences.getInt(SHARED_PREFERENCES_PLAYBACK_IS_PLAYING, -1)
        return PlaybackState.parse(stateValue) ?: PlaybackState.Stopped()
    }

    fun getCurrentSong(): String {
        return sharedPreferences.getString(SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING, "") ?: ""
    }
}