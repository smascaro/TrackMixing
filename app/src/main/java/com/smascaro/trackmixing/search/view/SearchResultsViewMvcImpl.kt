package com.smascaro.trackmixing.search.view

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.*
import com.smascaro.trackmixing.common.view.architecture.BaseObservableViewMvc
import com.smascaro.trackmixing.player.business.downloadtrack.TrackDownloadService
import com.smascaro.trackmixing.search.model.SearchResult
import javax.inject.Inject
import kotlin.concurrent.thread

class SearchResultsViewMvcImpl @Inject constructor(
    private val searchResultsAdapter: SearchResultsAdapter,
    private val uiUtils: UiUtils,
    resourcesWrapper: ResourcesWrapper
) : BaseObservableViewMvc<SearchResultsViewMvc.Listener>(),
    SearchResultsAdapter.Listener,
    SearchResultsViewMvc,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var resultsRecyclerView: RecyclerView

    private lateinit var searchInputLayout: TextInputLayout
    private lateinit var searchInputText: TextInputEditText

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
        resultsRecyclerView = findViewById(R.id.rv_search_results)
        backgroundGradient = findViewById(R.id.v_background_gradient)
        resultsRecyclerView.layoutManager = LinearLayoutManager(getContext())
        searchResultsAdapter.setOnSearchResultClickedListener(this)
        resultsRecyclerView.adapter = searchResultsAdapter

        searchInputLayout = findViewById(R.id.il_search_input_layout)
        searchInputText = findViewById(R.id.et_search_input)
        searchInputText.setOnFocusChangeListener { v, hasFocus ->
            searchInputLayout.setStartIconDrawable(
                if (hasFocus) {
                    R.drawable.ic_back_24dp
                } else {
                    R.drawable.ic_search_24dp
                }
            )
        }

        searchInputText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getListeners().forEach {
                    val queryText = getTextInSearchInput()
                    it.onSearchButtonClicked(queryText)
                }

                hideKeyboard()
                v.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        resultsRecyclerView.setOnTouchListener { v, event ->
            searchInputText.clearFocus()
            return@setOnTouchListener false
        }
        searchInputText.setOnTouchListener { v, event ->
            val DRAWABLE_LEFT_IDX = 0
            val DRAWABLE_TOP_IDX = 1
            val DRAWABLE_RIGHT_IDX = 2
            val DRAWABLE_BOTTOM_IDX = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (searchInputText.hasFocus()) {
                    if (event.rawX <= searchInputText.compoundDrawables[DRAWABLE_LEFT_IDX].bounds.right) {
                        hideKeyboard()
                        searchInputText.clearFocus()
                        true
                    }
                }
            }

            false
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

    private fun hideKeyboard() {
        uiUtils.hideKeyboard(getRootView())
    }

    private fun getTextInSearchInput(): String {
        return searchInputText.text?.toString() ?: ""
    }

    override fun onSearchResultClicked(searchResult: SearchResult) {
        showMessage("Requesting \"${searchResult.title}\"")
        searchInputText.clearFocus()
        getListeners().forEach {
            it.onSearchResultClicked(searchResult)
        }
    }

    override fun bindResults(results: List<SearchResult>) {
        searchResultsAdapter.bindResults(results)
    }

    override fun showMessage(message: String) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun startRequest(url: String) {
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
                Color.BLACK
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