package com.smascaro.trackmixing.playback.di.module

import android.content.Context
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.di.module.notification.PlayerNotificationHelperImplementation
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.playback.di.SessionScope
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class PlaybackModule {

    @SessionScope
    @Provides
    @PlayerNotificationHelperImplementation
    fun providePlayerNotificationHelper(
        context: Context,
        requestManager: RequestManager,
        ui: MainCoroutineScope,
        playbackSession: com.smascaro.trackmixing.playback.utils.PlaybackSession
    ): NotificationHelper {
        return com.smascaro.trackmixing.playback.utils.PlayerNotificationHelper(
            context,
            requestManager,
            ui,
            playbackSession
        )
    }
    @Module
    interface Bindings {
        @SessionScope
        @Binds
        fun providePlaybackSession(playbackSessionImpl: com.smascaro.trackmixing.playback.utils.PlaybackSessionImpl): com.smascaro.trackmixing.playback.utils.PlaybackSession
    }
}