package com.smascaro.trackmixing.main.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.playback.utils.PlaybackStateManager
import com.smascaro.trackmixing.base.utils.ResourcesWrapper
import com.smascaro.trackmixing.playback.utils.SharedPreferencesFactory
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservableViewMvc
import javax.inject.Inject

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
    private var previousBackgroundGradientColor: Int = defaultGradientStartColor

    override fun initialize() {
        super.initialize()
        backgroundGradient = findViewById(R.id.v_background_gradient)
        setupSharedPreferences()
    }

    private fun setupSharedPreferences() {
        sharedPreferences =
            com.smascaro.trackmixing.playback.utils.SharedPreferencesFactory.getPlaybackSharedPreferencesFactory(getContext()!!)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key != null && key == com.smascaro.trackmixing.playback.utils.PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_SONG_PLAYING || key == com.smascaro.trackmixing.playback.utils.PlaybackStateManager.SHARED_PREFERENCES_PLAYBACK_IS_PLAYING) {
            getListeners().forEach {
                it.onPlayerStateChanged()
            }
        }
    }

    override fun showMessage(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun updateBackgroundColor(newBackgroundColor: Int) {
        animateBackgroundGradientTo(newBackgroundColor)
    }

    override fun updateBackgroundColorToDefault() {
        animateBackgroundGradientTo(defaultGradientStartColor)
    }

    private fun animateBackgroundGradientTo(newBackgroundColor: Int) {
        val backgroundDrawable = backgroundGradient.background as GradientDrawable
        val initialColor = previousBackgroundGradientColor
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
        valueAnimator.doOnEnd {
            previousBackgroundGradientColor = newBackgroundColor
        }
        valueAnimator.start()
    }
}