package com.smascaro.trackmixing.main.components.player.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import com.smascaro.trackmixing.R
import timber.log.Timber

class VerticalSeekbar(context: Context, attributeSet: AttributeSet) :
    AppCompatSeekBar(context, attributeSet) {
    enum class Position { CENTER_FIXED, DYNAMIC_TRESPASS, DYNAMIC_NO_TRESSPASS }

    private val FLAG_TRESPASS_START = 1
    private val FLAG_TRESPASS_END = 2
    var showNumericProgress: Boolean = false
    var progressPosition: Position = Position.CENTER_FIXED
    var preffixText: String = ""
    var suffixText: String = ""
    var trespassMode: Int = 0
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 18f * context.resources.displayMetrics.scaledDensity
        style = Paint.Style.FILL
    }

    private var text = ""
    private val textRect = Rect()

    private var textX = 0f
    private var textY = 0f

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.VerticalSeekbar,
            0, 0
        ).apply {
            try {
                showNumericProgress = getBoolean(R.styleable.VerticalSeekbar_showNumericProgress, false)
                progressPosition = Position.values()[getInt(R.styleable.VerticalSeekbar_progress_position, 0)]
                preffixText = getString(R.styleable.VerticalSeekbar_preffixText) ?: ""
                suffixText = getString(R.styleable.VerticalSeekbar_suffixText) ?: ""
                trespassMode = getInteger(R.styleable.VerticalSeekbar_trespass_mode, 0)
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = suggestedMinimumWidth + paddingLeft + paddingRight
        val height = suggestedMinimumHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            getDefaultSize(width, widthMeasureSpec),
            getDefaultSize(height, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.rotate(-90f)
        canvas?.translate(-height.toFloat(), 0f)
        super.onDraw(canvas)

        canvas?.save()
        if (showNumericProgress) {
            text = "$preffixText$progress$suffixText"
            determineTextPosition()
            canvas?.translate(textX, textY)
            canvas?.rotate(90f)
            canvas?.drawText(text, 0f, 0f, textPaint)
            canvas?.restore()
        }
    }

    private fun determineTextPosition() {
        textPaint.getTextBounds(text, 0, text.length, textRect)
        when (progressPosition) {
            Position.CENTER_FIXED -> determineTextPositionCenterFixed()
            Position.DYNAMIC_TRESPASS -> determineTextPositionDynamicTrespassing()
            Position.DYNAMIC_NO_TRESSPASS -> determineTextPositionDynamicNoTrespassing()
        }
    }

    private fun determineTextPositionCenterFixed() {
        val percentageProgress = getProgressPercentage()
        textPaint.color = if (percentageProgress <= 0.50) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        textX = height * 0.5f + textRect.exactCenterY()
        textY = width * 0.5f - textRect.exactCenterX()
    }

    private fun determineTextPositionDynamicTrespassing() {
        if (trespassMode == 0) {
            determineTextPositionDynamicNoTrespassing()
            return
        }
        val percentageProgress = getProgressPercentage()
        val topBorderY = (height - 2 * paddingEnd).toFloat()
        val bottomBorderY = paddingStart.toFloat()
        val maxTop = if (checkFlag(FLAG_TRESPASS_END)) {
            height - paddingEnd
        } else {
            height - 2 * paddingEnd
        }
        val maxBottom = if (checkFlag(FLAG_TRESPASS_START)) {
            0f
        } else {
            2f * paddingStart - textRect.height()
        }
        val x = width * 0.5f
        val y = maxBottom + percentageProgress * (maxTop - maxBottom)
        textPaint.color =
            if (y > topBorderY) Color.WHITE
            else if (y in bottomBorderY..topBorderY) {
                // if trespass end is enabled and start disabled, text and progress will never collide
                if (checkFlag(FLAG_TRESPASS_END) && !checkFlag(FLAG_TRESPASS_START)) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }
            } else Color.WHITE
        this.textX = y
        this.textY = x - textRect.exactCenterX()
    }

    private fun determineTextPositionDynamicNoTrespassing() {
        val percentageProgress = getProgressPercentage()
        val maxTop = height.toFloat() - 2 * paddingEnd
        val maxBottom = 2f * paddingStart - textRect.height()
        val x = width * 0.5f
        val y = maxBottom + percentageProgress * (maxTop - maxBottom)
        textPaint.color = if (percentageProgress <= 0.5) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        this.textX = y
        this.textY = x - textRect.exactCenterX()
    }

    private fun getProgressPercentage() = (progress.toFloat() / max.toFloat())

    private fun checkFlag(flag: Int): Boolean {
        Timber.v("CheckFlag - TrespassMode: $trespassMode - Flag: $flag --> Result: ${(trespassMode or flag) == trespassMode}")
        return (trespassMode or flag) == trespassMode
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (!isEnabled) {
            false
        } else {
            when (event?.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_UP -> {
                    progress = max - (max * event.y / height).toInt()
                    onSizeChanged(width, height, 0, 0)
                }
            }
            true
        }
    }
}