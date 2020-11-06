package com.smascaro.trackmixing.search.di.module

import android.content.Context
import com.smascaro.trackmixing.base.di.MainScope
import com.smascaro.trackmixing.base.di.module.notification.DownloadNotificationHelperImplementation
import com.smascaro.trackmixing.base.utils.NotificationHelper
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelper
import com.smascaro.trackmixing.base.utils.navigation.NavigationHelperImpl
import com.smascaro.trackmixing.search.business.download.utils.DownloadNotificationHelper
import com.smascaro.trackmixing.search.view.SearchResultsViewMvc
import com.smascaro.trackmixing.search.view.SearchResultsViewMvcImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class SearchModule {
    @MainScope
    @Provides
    @DownloadNotificationHelperImplementation
    fun provideDownloadNotificationHelper(context: Context): NotificationHelper {
        return DownloadNotificationHelper(context)
    }

    @Module
    interface Bindings {
        @MainScope
        @Binds
        fun provideSearchResultsViewMvc(searchResultsViewMvcImpl: SearchResultsViewMvcImpl): SearchResultsViewMvc

        @MainScope
        @Binds
        fun provideNavigationHelper(navigationHelperImpl: NavigationHelperImpl): NavigationHelper
    }
}
// @Module
// class SearchCoroutineScopes {
//     @MainScope
//     @Provides
//     fun provideMainCoroutineScope(): MainCoroutineScope {
//         return object : MainCoroutineScope {
//             override val coroutineContext = Job() + Dispatchers.Main
//         }
//     }
//
//     @MainScope
//     @Provides
//     fun provideIoCoroutineScope(): IoCoroutineScope {
//         return object : IoCoroutineScope {
//             override val coroutineContext = Job() + Dispatchers.IO
//         }
//     }
//     // @MainScope
//     // @Provides
//     // fun provideGlideInstance(context: Context): RequestManager {
//     //     return Glide.with(context)
//     // }
//
// }
