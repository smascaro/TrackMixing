package com.smascaro.trackmixing.base.di.component

import android.content.Context
import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.coroutine.IoCoroutineScope
import com.smascaro.trackmixing.base.coroutine.MainCoroutineScope
import com.smascaro.trackmixing.base.di.module.CoroutineScopesModule
import com.smascaro.trackmixing.base.di.module.GlideModule
import com.smascaro.trackmixing.base.di.module.NetworkModule
import com.smascaro.trackmixing.base.network.node.api.NodeApi
import com.smascaro.trackmixing.base.network.node.api.NodeDownloadsApi
import com.smascaro.trackmixing.base.network.testdata.api.TestDataApi
import com.smascaro.trackmixing.base.network.testdata.api.TestDataFilesApi
import com.smascaro.trackmixing.base.network.youtube.api.YoutubeApi
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, CoroutineScopesModule::class, GlideModule::class])
interface BaseComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withContext(context: Context): Builder
        fun build(): BaseComponent
    }

    val youtubeApi: YoutubeApi
    val nodeApi: NodeApi
    val nodeDownloadsApi: NodeDownloadsApi
    val testDataApi: TestDataApi
    val testDataFilesApi: TestDataFilesApi
    val glide: RequestManager
    val ui: MainCoroutineScope
    val io: IoCoroutineScope
}