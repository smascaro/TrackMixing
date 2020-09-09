package com.smascaro.trackmixing.trackslist.controller

import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.trackslist.business.FetchDownloadedTracks
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TracksListControllerTest {
    private lateinit var SUT: TracksListController

    // region constants

    // endregion constants

    // region helper fields
    @Mock private lateinit var fetchDownloadedTracksUseCase: FetchDownloadedTracks
    private lateinit var navigationHelperTd: NavigationHelperTd
    // endregion helper fields

    @Before
    fun setup() {
        navigationHelperTd = NavigationHelperTd()
        SUT = TracksListController(fetchDownloadedTracksUseCase, navigationHelperTd)
    }

    // region tests
    /****LIFECYCLE EVENTS****/
    //view listener is registered at start
    //use case listener is registered at start
    //downloaded tracks are loaded on start
    //view listener is unregistered on stop
    //use case listener is unregistered on stop

    /****USER EVENTS****/
    //user clicks on search button - navigation to search fragment
    //user clicks on a track -

    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes
    class NavigationHelperTd : NavigationHelper {
        override fun navigateTo(destination: Int) {
            TODO("Not yet implemented")
        }

        override fun bindNavController(navController: NavController) {
            TODO("Not yet implemented")
        }

        override fun toDetails(track: Track, extras: FragmentNavigator.Extras) {
            TODO("Not yet implemented")
        }

        override fun toPlayer(track: Track) {
            TODO("Not yet implemented")
        }

        override fun toSearch() {
            TODO("Not yet implemented")
        }

        override fun back() {
            TODO("Not yet implemented")
        }

    }
    // endregion helper classes
}