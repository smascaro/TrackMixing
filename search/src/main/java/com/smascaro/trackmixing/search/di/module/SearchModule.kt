package com.smascaro.trackmixing.search.di.module

import android.content.Context
import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.di.module.notification.DownloadNotificationHelperImplementation
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.search.business.download.utils.DownloadNotificationHelper
import com.smascaro.trackmixing.search.view.SearchResultsViewMvc
import com.smascaro.trackmixing.search.view.SearchResultsViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @Provides
    @DownloadNotificationHelperImplementation
    fun provideDownloadNotificationHelper(context: Context): NotificationHelper {
        return DownloadNotificationHelper(context)
    }

    @Module
    interface Bindings {
        @Binds
        fun provideSearchResultsViewMvc(searchResultsViewMvcImpl: SearchResultsViewMvcImpl): SearchResultsViewMvc
    }
}
