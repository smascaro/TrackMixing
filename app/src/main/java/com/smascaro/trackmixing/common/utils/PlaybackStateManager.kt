package com.smascaro.trackmixing.common.utils

import android.content.Context
import com.smascaro.trackmixing.common.data.datasource.repository.TracksRepository
import com.smascaro.trackmixing.common.data.datasource.repository.toModel
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.di.coroutines.IoCoroutineScope
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

class PlaybackStateManager @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val io: IoCoroutineScope,
    context: Context
) {
    private val sharedPreferences =
        SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(
            context
        )

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
        EventBus.getDefault().post(PlaybackEvent.StateChanged(state))
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
                -1
            )
        }
        return PlaybackState.parse(
            stateValue
        )
            ?: PlaybackState.Stopped()
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

    fun setCurrentPlayingTimestamp(timestampMillis: Long) {
        sharedPreferences
            .edit()
            .putLong(SHARED_PREFERENCES_PLAYBACK_CURRENT_TIMESTAMP_MILLIS, timestampMillis)
            .apply()
    }

    fun getCurrentPlayingTimestampMillis(): Long {
        return sharedPreferences.getLong(SHARED_PREFERENCES_PLAYBACK_CURRENT_TIMESTAMP_MILLIS, 0)
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