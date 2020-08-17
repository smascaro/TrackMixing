package com.smascaro.trackmixing.ui.main

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.material.textview.MaterialTextView
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseObservableViewMvc
import com.smascaro.trackmixing.ui.common.UiUtils
import javax.inject.Inject

class BottomPlayerViewMvcImpl @Inject constructor(
    private val uiUtils: UiUtils,
    private val glide: RequestManager
) :
    BaseObservableViewMvc<BottomPlayerViewMvc.Listener>(),
    BottomPlayerViewMvc {

    private lateinit var bottomBar: ConstraintLayout
    private lateinit var bottomBarTextView: MaterialTextView
    private lateinit var bottomBarBackgroundImageView: ImageView

    private val bottomBarHeight = uiUtils.DpToPixels(80f)

    override fun bindRootView(rootView: View?) {
        super.bindRootView(rootView)
        initialize()
    }

    private fun initialize() {
        val bottomBarWrapper = findViewById<ConstraintLayout>(R.id.layout_player_actions_bottom)
        LayoutInflater.from(getContext())
            .inflate(R.layout.layout_actions_bottom, bottomBarWrapper, false)
        bottomBar = bottomBarWrapper.findViewById(R.id.layout_player_actions_bottom)
        bottomBarTextView = bottomBarWrapper.findViewById(R.id.tv_track_title_player_bottom)
        bottomBarBackgroundImageView =
            bottomBarWrapper.findViewById(R.id.iv_background_player_bottom)
        bottomBar.setOnClickListener {
            getListeners().forEach {
                it.onLayoutClick()
            }
        }
    }

    override fun showPlayerBar(data: BottomPlayerData) {
        bottomBarTextView.text = data.title
        glide
            .asBitmap()
            .load(data.thumbnailUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(BitmapImageViewTarget(bottomBarBackgroundImageView))
        bottomBar.visibility = View.VISIBLE
        val layoutParams = bottomBar.layoutParams
        layoutParams.height = bottomBarHeight.toInt()
        bottomBar.layoutParams = layoutParams
    }

    override fun hidePlayerBar() {
        val layoutParams = bottomBar.layoutParams
        layoutParams.height = 0
        bottomBar.layoutParams = layoutParams
        bottomBar.visibility = View.GONE
    }

}