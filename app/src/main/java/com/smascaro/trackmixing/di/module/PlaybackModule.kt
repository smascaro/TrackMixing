package com.smascaro.trackmixing.di.module

import android.content.Context
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.di.module.notification.PlayerNotificationHelperImplementation
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.playback.service.MixPlayerService
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.playback.utils.media.PlaybackSessionImpl
import com.smascaro.trackmixing.playback.utils.notification.PlayerNotificationHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
abstract class PlaybackModule {
    @ContributesAndroidInjector
    abstract fun mixPlayerService(): MixPlayerService

    companion object {
        @Singleton
        @Provides
        @PlayerNotificationHelperImplementation
        fun providePlayerNotificationHelper(
            context: Context,
            requestManager: RequestManager,
            ui: com.smascaro.trackmixing.base.coroutine.MainCoroutineScope,
            playbackSession: PlaybackSession
        ): NotificationHelper {
            return PlayerNotificationHelper(
                requestManager,
                ui,
                playbackSession,
                context
            )
        }
    }

    @Module
    interface Bindings {
        @Binds
        fun providePlaybackSession(playbackSessionImpl: PlaybackSessionImpl): PlaybackSession
    }
}