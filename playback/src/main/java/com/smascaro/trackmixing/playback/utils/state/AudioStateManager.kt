package com.smascaro.trackmixing.playback.utils.state

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import com.smascaro.trackmixing.base.ui.architecture.view.BaseObservable
import timber.log.Timber
import javax.inject.Inject

class AudioStateManager @Inject constructor(context: Context) :
    BaseObservable<AudioStateManager.Listener>(),
    AudioManager.OnAudioFocusChangeListener {
    interface Listener {
        fun onAudioFocusLoss()
        fun onAudioFocusTransientLoss()
    }

    private val audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val focusRequest: AudioFocusRequest? =
        buildFocusRequest()

    private fun buildFocusRequest(): AudioFocusRequest? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
                setAudioAttributes(buildAudioAttributes())
                setOnAudioFocusChangeListener(this@AudioStateManager)
            }.build()
        } else {
            null
        }
    }

    private fun buildAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder().apply {
            setUsage(AudioAttributes.USAGE_MEDIA)
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        }.build()
    }

    fun requestAudioFocus(): Boolean {
        return if (focusRequest != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                audioManager.requestAudioFocus(
                    this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } else {
            true
        }
    }

    fun abandonAudioFocus() {
        if (focusRequest != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(focusRequest)
            }
        } else {
            audioManager.abandonAudioFocus(this)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> handleAudioFocusGain()
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> handleAudioFocusLoss()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> handleAudioFocusTransientLoss()
        }
    }

    private fun handleAudioFocusTransientLoss() {
        Timber.d("Audio focus transient loss")
        getListeners().forEach {
            it.onAudioFocusTransientLoss()
        }
    }

    private fun handleAudioFocusLoss() {
        Timber.d("Audio focus lost")
        getListeners().forEach {
            it.onAudioFocusLoss()
        }
    }

    private fun handleAudioFocusGain() {
        Timber.d("Audio focus gained")
    }
}