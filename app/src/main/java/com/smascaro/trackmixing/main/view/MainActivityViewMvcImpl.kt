package com.smascaro.trackmixing.main.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.iterator
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING
import com.smascaro.trackmixing.common.utils.SharedPreferencesFactory
import com.smascaro.trackmixing.common.utils.ui.UiUtils
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.common.view.ui.BaseActivity
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivityViewMvcImpl @Inject constructor(
    private val uiUtils: UiUtils,
    resourcesWrapper: ResourcesWrapper
) :
    MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toolbarTitleText: MaterialTextView
    private lateinit var toolbarBackButtonImageView: ImageView
    private lateinit var backgroundGradient: View
    private lateinit var toolbarSearchInputLayout: TextInputLayout

    private lateinit var activity: BaseActivity
    private val gradientCenterColor =
        resourcesWrapper.getColor(R.color.track_player_background_gradient_center_color)
    private val gradientEndColor =
        resourcesWrapper.getColor(R.color.track_player_background_gradient_end_color)
    private val defaultGradientStartColor = resourcesWrapper.getColor(R.color.colorAccent)

    private lateinit var sharedPreferences: SharedPreferences

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
        initializeListeners()
    }

    override fun bindActivity(activity: BaseActivity) {
        this.activity = activity
    }

    private fun initializeListeners() {
        backgroundGradient = findViewById(R.id.v_background_gradient)
        toolbarBackButtonImageView.setOnClickListener {
            getListeners().forEach {
                it.onToolbarBackButtonPressed()
            }
        }
        setupSharedPreferences()
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
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
        toolbarSearchInputLayout = toolbar.findViewById(R.id.toolbar_search_input_layout)

        toolbar.inflateMenu(R.menu.options_menu_main)
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

    override fun updateBackgroundColor(newBackgroundColor: Int) {
        animateBackgroundGradientTo(newBackgroundColor)
    }

    override fun updateBackgroundColorToDefault() {
        animateBackgroundGradientTo(defaultGradientStartColor)
    }

    private fun animateBackgroundGradientTo(newBackgroundColor: Int) {
        val backgroundDrawable = backgroundGradient.background as GradientDrawable
        val initialColor =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                backgroundDrawable.colors?.first() ?: Color.BLACK
            } else {
                Color.BLACK //TODO mantenir l'estat anterior per a pre-Nougat
            }
        val valueAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), initialColor, newBackgroundColor)
        valueAnimator.duration = 700
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        val colorsArray =
            listOf(initialColor, gradientCenterColor, gradientEndColor).toIntArray()
        valueAnimator.addUpdateListener {
            colorsArray[0] = it.animatedValue as Int
            backgroundDrawable.colors = colorsArray
        }
        valueAnimator.start()
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
        toolbarSearchInputLayout.visibility = View.VISIBLE
    }

    override fun prepareTracksListContextLayout() {
        hideSearchInput()
        showMenu()
        showTitle()
    }

    private fun hideSearchInput() {
        toolbarSearchInputLayout.visibility = View.GONE
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
}