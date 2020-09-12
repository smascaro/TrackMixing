package com.smascaro.trackmixing.main.view

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivityViewMvcImpl @Inject constructor(private val uiUtils: UiUtils) :
    MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>() {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toolbarTitleText: MaterialTextView
    private lateinit var toolbarBackButtonImageView: ImageView

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
        initializeListeners()
    }

    private fun initializeListeners() {
        toolbarBackButtonImageView.setOnClickListener {
            getListeners().forEach {
                it.onToolbarBackButtonPressed()
            }
        }
    }

    override fun updateTitle(title: String, enableBackNavigation: Boolean) {
        toolbarTitleText.text = title
        if (enableBackNavigation) {
            toolbarBackButtonImageView.visibility = View.VISIBLE
        } else {
            toolbarBackButtonImageView.visibility = View.GONE
        }
    }

    override fun cleanUp() {
        uiUtils.hideKeyboard(getRootView())
    }

    private fun initialize() {
        toolbar = findViewById(R.id.toolbar)
        toolbarTitleText = toolbar.findViewById(R.id.tv_toolbar_title)
        toolbarBackButtonImageView = toolbar.findViewById(R.id.iv_toolbar_back_button)
        toolbar.inflateMenu(R.menu.options_menu_main)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.destination_settings) {
                navigateToSettings()
            } else {
                false
            }
        }
    }

    private fun navigateToSettings(): Boolean {
        getListeners().forEach {
            it.onSettingsMenuButtonClicked()
        }
        return true
    }

    override fun showMessage(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun startProcessingRequest(url: String) {
        if (getContext() != null) {
            thread {
                TrackDownloadService.start(getContext()!!, url)
            }
        }
    }

}