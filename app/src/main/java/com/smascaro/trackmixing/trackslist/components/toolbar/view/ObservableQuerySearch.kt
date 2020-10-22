package com.smascaro.trackmixing.trackslist.components.toolbar.view

interface ObservableQuerySearch {
    interface QuerySearchListener {
        fun onQuerySearchExecute(query: String)
    }

    fun registerQuerySearchListener(listener: QuerySearchListener)
    fun unregisterQuerySearchListener(listener: QuerySearchListener)
}