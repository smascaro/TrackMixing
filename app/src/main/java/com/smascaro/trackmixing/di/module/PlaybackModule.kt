package com.smascaro.trackmixing.di.module

import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.di.module.notification.PlayerNotificationHelperImplementation
import com.smascaro.trackmixing.playback.utils.media.PlaybackSession
import com.smascaro.trackmixing.playback.utils.media.PlaybackSessionImpl
import com.smascaro.trackmixing.playback.utils.notification.PlayerNotificationHelper
import dagger.Binds
import dagger.Module

@Module
abstract class PlaybackModule {

    @Module
    interface Bindings {
        @Binds
        fun providePlaybackSession(playbackSessionImpl: PlaybackSessionImpl): PlaybackSession

        @Binds
        @PlayerNotificationHelperImplementation
        fun providePlayerNotificationHelper(playerNotificationHelper: PlayerNotificationHelper): NotificationHelper
    }
}