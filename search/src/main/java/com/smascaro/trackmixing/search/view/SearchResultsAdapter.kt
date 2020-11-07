package com.smascaro.trackmixing.search.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smascaro.trackmixing.search.R
import com.smascaro.trackmixing.search.di.SearchResultItemViewMvcFactory
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.view.resultitem.SearchResultsItemViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultsAdapter @Inject constructor(
    private val viewMvcFactory: SearchResultItemViewMvcFactory,
) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>(), SearchResultsItemViewMvc.Listener {
    interface Listener {
        fun onSearchResultClicked(searchResult: SearchResult)
    }

    class ViewHolder(val itemViewMvc: SearchResultsItemViewMvc) :
        RecyclerView.ViewHolder(itemViewMvc.getRootView())

    private var listener: Listener? = null
    private var searchResults = mutableListOf<SearchResult>()

    fun setOnSearchResultClickedListener(listener: Listener) {
        this.listener = listener
    }

    fun removeOnSearchResultClickedListener() {
        this.listener = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemViewMvc = viewMvcFactory.create()
        itemViewMvc.bindRootView(
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        )
        itemViewMvc.registerListener(this)
        return ViewHolder(itemViewMvc)
    }

    fun bindResults(results: List<SearchResult>) {
        searchResults = results.toMutableList()
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemViewMvc.bindResult(searchResults[position])
        holder.itemViewMvc.bindPosition(holder.adapterPosition)
    }

    override fun onSearchResultClicked(result: SearchResult) {
        listener?.onSearchResultClicked(result)
    }
}