package com.smascaro.trackmixing.common.di

import android.content.Context
import com.smascaro.trackmixing.base.di.component.BaseComponent
import com.smascaro.trackmixing.common.di.main.MainScope
import com.smascaro.trackmixing.search.business.download.TrackDownloadService
import dagger.BindsInstance
import dagger.Component

@MainScope
@Component(
    dependencies = [BaseComponent::class],
    modules = [AppModule::class, AppModule.StaticBindings::class, ViewMvcBuildersModule::class]
)
interface AppComponent {
//     @Component.Factory
//     interface Factory {
//         fun create(@BindsInstance context: Context): AppComponent
//     }

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(context: Context): Builder
        fun baseComponent(baseComponent: BaseComponent): Builder
        fun build(): AppComponent
    }

    // fun playerComponent(): PlayerComponent.Factory
// fun settingsComponent(): SettingsComponent.Factory
    fun inject(trackDownloadService: TrackDownloadService)
}