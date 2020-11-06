package com.smascaro.trackmixing.trackslist.components.toolbar.view

import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.base.utils.UiUtils
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import javax.inject.Inject

class ToolbarViewMvcImpl @Inject constructor(
    private val uiUtils: UiUtils,
) : ToolbarViewMvc,
    BaseObservableViewMvc<ToolbarViewMvc.Listener>() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun initialize() {
        super.initialize()
        toolbar = findViewById(R.id.toolbar)

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
}