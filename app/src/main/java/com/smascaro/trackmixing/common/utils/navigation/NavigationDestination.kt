package com.smascaro.trackmixing.common.utils.navigation

sealed class NavigationDestination {
    class TracksList : NavigationDestination()
    class Search : NavigationDestination()
    class Player : NavigationDestination()
    class Settings : NavigationDestination()
    class SelectTestData : NavigationDestination()
    class DownloadTestData : NavigationDestination()
}