package com.smascaro.trackmixing.main.components.toolbar.view

interface ObservableQuerySearch {
    interface QuerySearchListener {
        fun onQuerySearchExecute(query: String)
    }

    fun registerQuerySearchListener(listener: QuerySearchListener)
    fun unregisterQuerySearchListener(listener: QuerySearchListener)
}