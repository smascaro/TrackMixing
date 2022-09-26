package com.smascaro.trackmixing.base.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar
import com.smascaro.trackmixing.R

class PivotableSeekbar(context: Context, attributeSet: AttributeSet) :
    AppCompatSeekBar(context, attributeSet) {
    enum class Position { CENTER_FIXED, DYNAMIC_TRESPASS, DYNAMIC_NO_TRESPASS }
    enum class Orientation { HORIZONTAL, VERTICAL }

    private val FLAG_TRESPASS_START = 1
    private val FLAG_TRESPASS_END = 2
    var showNumericProgress: Boolean = false
    var progressPosition: Position = Position.CENTER_FIXED
    var prefixText: String = ""
    var suffixText: String = ""
    var trespassMode: Int = 0
    var orientation: Orientation = Orientation.HORIZONTAL
    var textSize: Float = 18f

    val verticalMode: Boolean
        get() = orientation == Orientation.VERTICAL
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private var text = ""
    private val textRect = Rect()

    private var textX = 0f
    private var textY = 0f

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.PivotableSeekbar,
            0, 0
        ).apply {
            try {
                showNumericProgress = getBoolean(R.styleable.PivotableSeekbar_showNumericProgress, false)
                progressPosition =
                    Position.values()[getInt(R.styleable.PivotableSeekbar_progress_position, 0)]
                prefixText = getString(R.styleable.PivotableSeekbar_prefixText) ?: ""
                suffixText = getString(R.styleable.PivotableSeekbar_suffixText) ?: ""
                trespassMode = getInteger(R.styleable.PivotableSeekbar_trespass_mode, 0)
                orientation = Orientation.values()[getInt(R.styleable.PivotableSeekbar_orientation, 0)]
                textSize = getDimension(R.styleable.PivotableSeekbar_textSize, 18f)
            } finally {
                recycle()
            }
        }
        textPaint.textSize = textSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (verticalMode) {
            super.onSizeChanged(h, w, oldh, oldw)
        } else {
            super.onSizeChanged(w, h, oldh, oldw)
        }
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
        if (verticalMode) {
            canvas?.rotate(-90f)
            canvas?.translate(-height.toFloat(), 0f)
        }
        super.onDraw(canvas)

        canvas?.save()
        if (showNumericProgress) {
            val percentageText = (getProgressPercentage() * 100).toInt()
            text = "$prefixText${percentageText}$suffixText"
            determineTextPosition()
            if (verticalMode) {
                canvas?.translate(textX, textY)
                canvas?.rotate(90f)
                canvas?.drawText(text, 0f, 0f, textPaint)
            } else {
                canvas?.drawText(text, textX, textY, textPaint)
            }
            canvas?.restore()
        }
    }

    private fun determineTextPosition() {
        textPaint.getTextBounds(text, 0, text.length, textRect)
        when (progressPosition) {
            Position.CENTER_FIXED -> determineTextPositionCenterFixed()
            Position.DYNAMIC_TRESPASS -> determineTextPositionDynamicTrespassing()
            Position.DYNAMIC_NO_TRESPASS -> determineTextPositionDynamicNoTrespassing()
        }
    }

    private fun determineTextPositionCenterFixed() {
        val percentageProgress = getProgressPercentage()
        if (verticalMode) {
            determineTextPositionCenterFixedVertical(percentageProgress)
        } else {
            determineTextPositionCenterFixedHorizontal(percentageProgress)
        }
    }

    private fun determineTextPositionCenterFixedHorizontal(percentageProgress: Float) {
        this.textX = width * 0.5f - textRect.exactCenterX()
        this.textY = height * 0.5f - textRect.exactCenterY()
        textPaint.color = if (percentageProgress <= 0.50) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }

    private fun determineTextPositionCenterFixedVertical(percentageProgress: Float) {
        this.textX = height * 0.5f + textRect.exactCenterY()
        this.textY = width * 0.5f - textRect.exactCenterX()
        textPaint.color = if (percentageProgress <= 0.50) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }

    private fun determineTextPositionDynamicTrespassing() {
        if (trespassMode == 0) {
            determineTextPositionDynamicNoTrespassing()
            return
        }
        val percentageProgress = getProgressPercentage()
        if (verticalMode) {
            determineTextPositionDynamicTrespassingVertical(percentageProgress)
        } else {
            //Horizontal mode
            determineTextPositionDynamicTrespassingHorizontal(percentageProgress)
        }
    }

    private fun determineTextPositionDynamicTrespassingHorizontal(percentageProgress: Float) {
        val endBorder = width - 2f * paddingEnd
        val startBorder = paddingStart.toFloat()
        val maxEnd = if (checkFlag(FLAG_TRESPASS_END)) {
            width - paddingEnd
        } else {
            width - 2 * paddingEnd
        }
        val maxStart = if (checkFlag(FLAG_TRESPASS_START)) {
            0f
        } else {
            2f * paddingStart - textRect.width()
        }
        val x = maxStart + percentageProgress * (maxEnd - maxStart) - textRect.exactCenterX()
        val y = height * 0.5f - textRect.exactCenterY()
        textPaint.color =
            if (x > endBorder) Color.WHITE
            else if (x in startBorder..endBorder) {
                // if trespass end is enabled and start disabled, text and progress will never collide
                if (checkFlag(FLAG_TRESPASS_END) && !checkFlag(FLAG_TRESPASS_START)) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }
            } else Color.WHITE
        this.textX = x
        this.textY = y
    }

    private fun determineTextPositionDynamicTrespassingVertical(percentageProgress: Float) {
        val endBorder = height - 2f * paddingEnd
        val startBorder = paddingStart.toFloat()
        val maxEnd = if (checkFlag(FLAG_TRESPASS_END)) {
            height - paddingEnd
        } else {
            height - 2 * paddingEnd
        }
        val maxStart = if (checkFlag(FLAG_TRESPASS_START)) {
            0f
        } else {
            2f * paddingStart - textRect.height()
        }
        val x = width * 0.5f - textRect.exactCenterX()
        val y = maxStart + percentageProgress * (maxEnd - maxStart)
        textPaint.color =
            if (y > endBorder) Color.WHITE
            else if (y in startBorder..endBorder) {
                // if trespass end is enabled and start disabled, text and progress will never collide
                if (checkFlag(FLAG_TRESPASS_END) && !checkFlag(FLAG_TRESPASS_START)) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }
            } else Color.WHITE
        this.textX = y
        this.textY = x
    }

    private fun determineTextPositionDynamicNoTrespassing() {
        val percentageProgress = getProgressPercentage()
        if (verticalMode) {
            determineTextPositionDynamicNoTrespassingVertical(percentageProgress)
        } else {
            determineTextPositionDynamicNoTrespassingHorizontal(percentageProgress)
        }
    }

    private fun determineTextPositionDynamicNoTrespassingHorizontal(percentageProgress: Float) {
        val maxEnd = width.toFloat() - paddingEnd - textRect.width()
        val maxStart = paddingStart
        val x = maxStart + percentageProgress * (maxEnd - maxStart)
        val y = height * 0.5f - textRect.exactCenterY()
        textPaint.color = if (percentageProgress <= 0.5) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        this.textX = x
        this.textY = y
    }

    private fun determineTextPositionDynamicNoTrespassingVertical(percentageProgress: Float) {
        val maxEnd = height.toFloat() - 2 * paddingEnd
        val maxStart = 2f * paddingStart - textRect.height()
        val x = maxStart + percentageProgress * (maxEnd - maxStart)
        val y = width * 0.5f - textRect.exactCenterX()
        textPaint.color = if (percentageProgress <= 0.5) {
            Color.WHITE
        } else {
            Color.BLACK
        }
        this.textX = x
        this.textY = y
    }

    private fun getProgressPercentage() = (progress.toFloat() / max.toFloat())

    private fun checkFlag(flag: Int): Boolean {
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
                    if (verticalMode) {
                        progress = max - (max * event.y / height).toInt()
                    } else {
                        progress = (max * event.x / width).toInt()
                    }
                    onSizeChanged(width, height, 0, 0)
                }
            }
            true
        }
    }
}