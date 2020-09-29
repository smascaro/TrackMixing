package com.smascaro.trackmixing.main.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING
import com.smascaro.trackmixing.common.utils.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING
import com.smascaro.trackmixing.common.utils.SharedPreferencesFactory
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import javax.inject.Inject
import kotlin.concurrent.thread

class MainActivityViewMvcImpl @Inject constructor(
    resourcesWrapper: ResourcesWrapper
) :
    MainActivityViewMvc,
    BaseObservableViewMvc<MainActivityViewMvc.Listener>(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var backgroundGradient: View

    private val gradientCenterColor =
        resourcesWrapper.getColor(R.color.track_player_background_gradient_center_color)
    private val gradientEndColor =
        resourcesWrapper.getColor(R.color.track_player_background_gradient_end_color)
    private val defaultGradientStartColor = resourcesWrapper.getColor(R.color.colorAccent)

    private lateinit var sharedPreferences: SharedPreferences

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        backgroundGradient = findViewById(R.id.v_background_gradient)
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
}