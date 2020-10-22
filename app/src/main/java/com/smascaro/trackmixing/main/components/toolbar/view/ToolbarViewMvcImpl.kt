package com.smascaro.trackmixing.main.components.toolbar.view

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.iterator
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ui.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.main.components.toolbar.view.ObservableQuerySearch.QuerySearchListener
import javax.inject.Inject

class ToolbarViewMvcImpl @Inject constructor(
    private val uiUtils: UiUtils,
) : ToolbarViewMvc,
    BaseObservableViewMvc<ToolbarViewMvc.Listener>() {
    private lateinit var activity: BaseActivity
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toolbarTitleText: MaterialTextView
    private lateinit var toolbarBackButtonImageView: ImageView
    private lateinit var toolbarSearchView: SearchView
    private var searchQueryListener: QuerySearchListener? = null

    override fun initialize() {
        super.initialize()
        toolbar = findViewById(R.id.toolbar)
        toolbarTitleText = toolbar.findViewById(R.id.tv_toolbar_title)
        toolbarBackButtonImageView = toolbar.findViewById(R.id.iv_toolbar_back_button)
        toolbarSearchView = toolbar.findViewById(R.id.toolbar_search_view)

        toolbar.inflateMenu(R.menu.options_menu_main)
    }

    override fun initializeListeners() {
        super.initializeListeners()
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.destination_settings) {
                navigateToSettings()
            } else if (it.itemId == R.id.destination_search) {
                navigateToSearch()
            } else {
                false
            }
        }
        toolbarBackButtonImageView.setOnClickListener {
            getListeners().forEach {
                it.onToolbarBackButtonPressed()
            }
        }
        toolbarSearchView.setOnCloseListener {
            toolbarSearchView.setQuery("", false)
            true
        }
        toolbarSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchQueryListener?.onQuerySearchExecute(toolbarSearchView.query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun navigateToSearch(): Boolean {
        getListeners().forEach {
            it.onSearchMenuButtonClicked()
        }
        return true
    }

    private fun navigateToSettings(): Boolean {
        getListeners().forEach {
            it.onSettingsMenuButtonClicked()
        }
        return true
    }

    override fun bindActivity(activity: BaseActivity) {
        this.activity = activity
    }

    override fun updateTitle(title: String, enableBackNavigation: Boolean) {
        toolbarTitleText.text = title
        if (enableBackNavigation) {
            toolbarBackButtonImageView.visibility = View.VISIBLE
        } else {
            toolbarBackButtonImageView.visibility = View.GONE
        }
    }

    override fun prepareSearchContextLayout() {
        hideMenu()
        hideTitle()
        showSearchInput()
    }

    private fun hideMenu() {
        val menuItems = toolbar.menu.iterator()
        menuItems.forEach {
            it.isVisible = false
        }
        activity.invalidateOptionsMenu()
    }

    private fun hideTitle() {
        toolbarTitleText.visibility = View.GONE
    }

    private fun showSearchInput() {
        toolbarSearchView.visibility = View.VISIBLE
        toolbarSearchView.requestFocus()
    }

    override fun prepareTracksListContextLayout() {
        hideSearchInput()
        showMenu()
        showTitle()
    }

    private fun hideSearchInput() {
        toolbarSearchView.visibility = View.GONE
    }

    private fun showMenu() {
        val menuItems = toolbar.menu.iterator()
        menuItems.forEach {
            it.isVisible = true
        }
        activity.invalidateOptionsMenu()
    }

    private fun showTitle() {
        toolbarTitleText.visibility = View.VISIBLE
    }

    override fun cleanUp() {
        uiUtils.hideKeyboard(getRootView())
    }

    override fun registerQuerySearchListener(listener: QuerySearchListener) {
        searchQueryListener = listener
    }

    override fun unregisterQuerySearchListener(listener: QuerySearchListener) {
        searchQueryListener = null
    }
}