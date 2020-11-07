package com.smascaro.trackmixing.playback.utils.state

import android.content.Context
import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.data.model.Track
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.data.repository.toModel
import com.smascaro.trackmixing.playback.model.TrackVolumeBundle
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class PlaybackStateManager @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val io: IoCoroutineScope,
    context: Context
) {
    companion object {
        const val SHARED_PREFERENCES_PLAYBACK = "SHARED_PREFERENCES_PLAYBACK"
        const val SHARED_PREFERENCES_PLAYBACK_IS_PLAYING = "SHARED_PREFERENCES_PLAYBACK_IS_PLAYING"
        const val SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING =
            "SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING"
        const val SHARED_PREFERENCES_PLAYBACK_CURRENT_TIMESTAMP_MILLIS =
            "SHARED_PREFERENCES_PLAYBACK_CURRENT_TIMESTAMP_MILLIS"
        const val SHARED_PREFERENCES_PLAYBACK_CURRENT_VOLUMES =
            "SHARED_PREFERENCES_PLAYBACK_CURRENT_VOLUMES"
    }

    private val sharedPreferences =
        SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(
            context
        )

    sealed class PlaybackState(protected val value: Int) {
        companion object {
            const val PLAYBACK_STATE_PLAYING = 0
            const val PLAYBACK_STATE_PAUSED = 1
            const val PLAYBACK_STATE_STOPPED = 2
            fun parse(stateValue: Int): PlaybackState {
                return when (stateValue) {
                    PLAYBACK_STATE_PLAYING -> Playing()
                    PLAYBACK_STATE_PAUSED -> Paused()
                    PLAYBACK_STATE_STOPPED -> Stopped()
                    else -> throw IllegalArgumentException("Invalid state value does not map to an existing state")
                }
            }
        }

        fun getIntValue() = value
        class Playing : PlaybackState(
            PLAYBACK_STATE_PLAYING
        )

        class Paused : PlaybackState(
            PLAYBACK_STATE_PAUSED
        )

        class Stopped : PlaybackState(
            PLAYBACK_STATE_STOPPED
        )
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

    suspend fun getPlayingState(): PlaybackState {
        val stateValue = withContext(io.coroutineContext) {
            sharedPreferences.getInt(
                SHARED_PREFERENCES_PLAYBACK_IS_PLAYING,
                PlaybackState.PLAYBACK_STATE_STOPPED
            )
        }
        return PlaybackState.parse(
            stateValue
        )
    }

    suspend fun getCurrentSong(): String {
        return withContext(io.coroutineContext) {
            sharedPreferences.getString(
                SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING,
                ""
            ) ?: ""
        }
    }

    suspend fun getCurrentTrack(): Track {
        val key = getCurrentSong()
        val track = tracksRepository.get(key)
        return track.toModel()
    }

    fun setCurrentPlayingVolumes(trackVolumeBundle: TrackVolumeBundle) {
        Timber.d("Storing volumes preferences")
        val jsonBundle = trackVolumeBundle.bundleAsString()
        sharedPreferences
            .edit()
            .putString(SHARED_PREFERENCES_PLAYBACK_CURRENT_VOLUMES, jsonBundle)
            .apply()
    }

    suspend fun getCurrentPlayingVolumes(): TrackVolumeBundle {
        val jsonBundle =
            withContext(io.coroutineContext) {
                sharedPreferences.getString(
                    SHARED_PREFERENCES_PLAYBACK_CURRENT_VOLUMES,
                    ""
                )
            }
        return if (jsonBundle == null || jsonBundle.isEmpty()) {
            TrackVolumeBundle(100, 100, 100, 100)
        } else {
            TrackVolumeBundle.parse(jsonBundle)
        }
    }
}