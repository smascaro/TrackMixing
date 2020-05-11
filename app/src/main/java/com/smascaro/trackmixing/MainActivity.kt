package com.smascaro.trackmixing

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mTracksPool: SoundPool
    private var mVocalsStreamId = 0
    private var mOtherStreamId = 0
    private var mBassStreamId = 0
    private var mDrumsStreamId = 0
    private var mVocalsVolume = 1.0f
    private var mOtherVolume = 1.0f
    private var mBassVolume = 1.0f
    private var mDrumsVolume = 1.0f
    private lateinit var mVocalsPlayer: MediaPlayer
    private lateinit var mOtherPlayer: MediaPlayer
    private lateinit var mBassPlayer: MediaPlayer
    private lateinit var mDrumsPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mVocalsPlayer = MediaPlayer.create(applicationContext, R.raw.vocals)
        mOtherPlayer = MediaPlayer.create(applicationContext, R.raw.other)
        mBassPlayer = MediaPlayer.create(applicationContext, R.raw.bass)
        mDrumsPlayer = MediaPlayer.create(applicationContext, R.raw.drums)

        btnPlayPauseMaster.setOnClickListener {
            if (mVocalsPlayer.isPlaying || mOtherPlayer.isPlaying || mBassPlayer.isPlaying || mDrumsPlayer.isPlaying) {
                mVocalsPlayer.pause()
                mOtherPlayer.pause()
                mBassPlayer.pause()
                mDrumsPlayer.pause()
                Toast.makeText(applicationContext, "Streams paused", Toast.LENGTH_SHORT).show()
            } else {
                mVocalsPlayer.start()
                mOtherPlayer.start()
                mBassPlayer.start()
                mDrumsPlayer.start()
                Toast.makeText(applicationContext, "Playing streams...", Toast.LENGTH_SHORT).show()
            }
        }

        btnPlayPauseOther.setOnClickListener {
            mOtherVolume = if (mOtherVolume > 0) {
                0.0f
            } else {
                1.0f
            }
            mOtherPlayer.setVolume(mOtherVolume, mOtherVolume)
        }
        btnVolDownOther.setOnClickListener {
            mOtherVolume -= 0.1f
            mOtherVolume = mOtherVolume.coerceAtLeast(0.0f)
            mOtherPlayer.setVolume(mOtherVolume, mOtherVolume)

        }
        btnVolUpOther.setOnClickListener {
            mOtherVolume += 0.1f
            mOtherVolume = mOtherVolume.coerceAtMost(1.0f)
            mOtherPlayer.setVolume(mOtherVolume, mOtherVolume)
        }
        btnPlayPauseVocals.setOnClickListener {
            mVocalsVolume = if (mVocalsVolume > 0) {
                0.0f
            } else {
                1.0f
            }
            mVocalsPlayer.setVolume(mVocalsVolume, mVocalsVolume)
        }
        btnVolDownVocals.setOnClickListener {
            mVocalsVolume -= 0.1f
            mVocalsVolume = mVocalsVolume.coerceAtLeast(0.0f)
            mVocalsPlayer.setVolume(mVocalsVolume, mVocalsVolume)

        }
        btnVolUpVocals.setOnClickListener {
            mVocalsVolume += 0.1f
            mVocalsVolume = mVocalsVolume.coerceAtMost(1.0f)
            mVocalsPlayer.setVolume(mVocalsVolume, mVocalsVolume)
        }

        btnPlayPauseBass.setOnClickListener {
            mBassVolume = if (mBassVolume > 0) {
                0.0f
            } else {
                1.0f
            }
            mBassPlayer.setVolume(mBassVolume, mBassVolume)
        }
        btnVolDownBass.setOnClickListener {
            mBassVolume -= 0.1f
            mBassVolume = mBassVolume.coerceAtLeast(0.0f)
            mBassPlayer.setVolume(mBassVolume, mBassVolume)

        }
        btnVolUpBass.setOnClickListener {
            mBassVolume += 0.1f
            mBassVolume = mBassVolume.coerceAtMost(1.0f)
            mBassPlayer.setVolume(mBassVolume, mBassVolume)
        }

        btnPlayPauseDrums.setOnClickListener {
            mDrumsVolume = if (mDrumsVolume > 0) {
                0.0f
            } else {
                1.0f
            }
            mDrumsPlayer.setVolume(mDrumsVolume, mDrumsVolume)
        }
        btnVolDownDrums.setOnClickListener {
            mDrumsVolume -= 0.1f
            mDrumsVolume = mDrumsVolume.coerceAtLeast(0.0f)
            mDrumsPlayer.setVolume(mDrumsVolume, mDrumsVolume)

        }
        btnVolUpDrums.setOnClickListener {
            mDrumsVolume += 0.1f
            mDrumsVolume = mDrumsVolume.coerceAtMost(1.0f)
            mDrumsPlayer.setVolume(mDrumsVolume, mDrumsVolume)
        }
    }

    override fun onStop() {
        mOtherPlayer.pause()
        mVocalsPlayer.pause()
        mBassPlayer.pause()
        mDrumsPlayer.pause()
        super.onStop()
    }

    override fun onDestroy() {
        mOtherPlayer.release()
        mVocalsPlayer.release()
        mBassPlayer.release()
        mDrumsPlayer.release()
        super.onDestroy()
    }
}
