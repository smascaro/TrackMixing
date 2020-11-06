package com.smascaro.trackmixing.search.di.component

import android.content.Context
import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.search.business.download.TrackDownloadService
import com.smascaro.trackmixing.search.di.module.SearchModule
import com.smascaro.trackmixing.search.view.SongSearchFragment
import dagger.BindsInstance
import dagger.Component

@MainScope
@Component(
    dependencies = [BaseComponent::class],
    modules = [SearchModule::class, SearchModule.Bindings::class]
)
interface SearchComponent {
    fun inject(searchFragment: SongSearchFragment)
    fun inject(trackDownloadService: TrackDownloadService)

    @Component.Builder
    interface Builder {
        fun withContext(@BindsInstance context: Context): Builder
        fun withBaseComponent(baseComponent: BaseComponent): Builder
        fun build(): SearchComponent
    }
}