package com.smascaro.trackmixing.base.di.component

import android.content.Context
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.data.repository.DownloadsDao
import com.smascaro.trackmixing.base.data.repository.TracksRepository
import com.smascaro.trackmixing.base.di.module.coroutine.CoroutineScopesModule
import com.smascaro.trackmixing.base.di.module.eventbus.EventBusModule
import com.smascaro.trackmixing.base.di.module.glide.GlideModule
import com.smascaro.trackmixing.base.di.module.navigation.NavigationModule
import com.smascaro.trackmixing.base.di.module.network.NetworkModule
import com.smascaro.trackmixing.base.di.module.repository.RepositoryModule
import com.smascaro.trackmixing.base.di.module.utils.UtilsModule
import com.smascaro.trackmixing.base.network.node.api.NodeApi
import com.smascaro.trackmixing.base.network.node.api.NodeDownloadsApi
import com.smascaro.trackmixing.base.network.testdata.api.TestDataApi
import com.smascaro.trackmixing.base.network.testdata.api.TestDataFilesApi
import com.smascaro.trackmixing.base.network.youtube.api.YoutubeApi
import com.smascaro.trackmixing.base.utils.FilesStorageHelper
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import dagger.BindsInstance
import dagger.Component
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        CoroutineScopesModule::class,
        GlideModule::class,
        RepositoryModule::class,
        RepositoryModule.Bindings::class,
        EventBusModule::class,
        UtilsModule::class,
        NavigationModule::class
    ]
)
interface BaseComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withContext(context: Context): Builder
        fun build(): BaseComponent
    }

    // Network
    val youtubeApi: YoutubeApi
    val nodeApi: NodeApi
    val nodeDownloadsApi: NodeDownloadsApi
    val testDataApi: TestDataApi
    val testDataFilesApi: TestDataFilesApi

    // Glide
    val glide: RequestManager

    // Coroutine
    val ui: com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
    val io: com.smascaro.trackmixing.base.coroutine.IoCoroutineScope

    // Repository
    val tracksRepository: TracksRepository
    val downloadsDao: DownloadsDao

    // EventBus
    val eventBus: EventBus

    // Utils
    val filesStorageHelper: FilesStorageHelper

    // Navigation
    val navigationHelper: NavigationHelper
}