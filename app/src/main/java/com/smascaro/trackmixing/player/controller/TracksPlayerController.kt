package com.smascaro.trackmixing.player.controller

import com.smascaro.trackmixing.common.controller.BaseController
import com.smascaro.trackmixing.common.data.model.Track
import com.smascaro.trackmixing.common.error.NoLoadedTrackException
import com.smascaro.trackmixing.common.utils.ui.ColorExtractor
import com.smascaro.trackmixing.common.utils.PlaybackStateManager
import com.smascaro.trackmixing.playbackservice.model.PlaybackEvent
import com.smascaro.trackmixing.playbackservice.model.TrackInstrument
import com.smascaro.trackmixing.playbackservice.utils.PlaybackSession
import com.smascaro.trackmixing.player.view.TracksPlayerViewMvc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class TracksPlayerController @Inject constructor(
    private val playbackSession: PlaybackSession,
//    private val glide: RequestManager,
    private val colorExtractor: ColorExtractor
) :
    BaseController<TracksPlayerViewMvc>(),
    TracksPlayerViewMvc.Listener {

    private lateinit var mTrack: Track
    private var isServiceStarted: Boolean = false
    fun bindTrack(track: Track) {
        mTrack = track
    }

    fun loadTrack() {
        if (!this::mTrack.isInitialized) {
            throw NoLoadedTrackException()
        }
        isServiceStarted = playbackSession.startPlayback(mTrack)

    }

    fun onDestroy() {
        viewMvc.unregisterListener(this)
        viewMvc.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    fun onCreate() {
        ensureViewMvcBound()
        viewMvc.bindVolumes(playbackSession.getVolumes())
        viewMvc.bindTrackDuration(mTrack.secondsLong)
        initializeActionButton()
        viewMvc.registerListener(this)
        EventBus.getDefault().register(this)
        CoroutineScope(Dispatchers.Main).launch {
            initializeBackgroundColor()
        }
    }

    private fun initializeBackgroundColor() {
        viewMvc.bindBackgroundColor(mTrack.backgroundColor)

//        glide
//            .asBitmap()
//            .load(mTrack.thumbnailUrl)
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onLoadCleared(placeholder: Drawable?) {
//                }
//
//                override fun onResourceReady(
//                    resource: Bitmap,
//                    transition: Transition<in Bitmap>?
//                ) {
//                    Palette.Builder(resource).generate {
//                        val color = it?.getLightVibrantColor(Color.WHITE) ?: Color.WHITE
//                    }
//                }
//            })
    }

    private fun initializeActionButton() {
        when (playbackSession.getState()) {
            is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
            is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
        }
    }

    private fun isServiceStarted(): Boolean {
        return isServiceStarted
    }

    fun playMaster() {
        if (isServiceStarted()) {
            playbackSession.play()
        } else {
            isServiceStarted = playbackSession.startPlayback(mTrack)
        }
    }

    override fun onServiceConnected() {
        loadTrack()
    }

    override fun onTrackVolumeChanged(trackInstrument: TrackInstrument, volume: Int) {
        playbackSession.setTrackVolume(trackInstrument, volume)
    }

    override fun onSeekRequestEvent(progress: Int) {
        playbackSession.seek(progress)
    }

    override fun onActionButtonClicked() {
        updateActionButton()
    }

    private fun updateActionButton() {
        when (playbackSession.getState()) {
            is PlaybackStateManager.PlaybackState.Playing -> handleActionButtonOnPlayingState()
            is PlaybackStateManager.PlaybackState.Paused -> handleActionButtonOnPausedState()
        }
    }

    private fun handleActionButtonOnPlayingState() {
        playbackSession.pause()
        viewMvc.showPlayButton()
    }

    private fun handleActionButtonOnPausedState() {
        playbackSession.play()
        viewMvc.showPauseButton()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PlaybackEvent.StateChanged) {
        when (event.newState) {
            is PlaybackStateManager.PlaybackState.Playing -> viewMvc.showPauseButton()
            is PlaybackStateManager.PlaybackState.Paused -> viewMvc.showPlayButton()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PlaybackEvent.TimestampChanged) {
        handleTimestampChanged(event.newTimestamp)
    }

    private fun handleTimestampChanged(timestamp: Int) {
        viewMvc.updateTimestamp(timestamp)
    }
}