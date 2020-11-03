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

class VerticalSeekbar(context: Context, attributeSet: AttributeSet) :
    AppCompatSeekBar(context, attributeSet) {

    var showNumericProgress: Boolean = false

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 18f * context.resources.displayMetrics.scaledDensity
        style = Paint.Style.FILL
    }

    private var text = ""
    private val textRect = Rect()

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.VerticalSeekbar,
            0, 0
        ).apply {
            try {
                showNumericProgress = getBoolean(R.styleable.VerticalSeekbar_showNumericProgress, false)
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

        val bottomBorderY = paddingStart
        canvas?.save()
        if (showNumericProgress) {
            text = "$progress"
            val percentageProgress = (progress.toFloat() / max.toFloat()) //0 to 1
            val textX = width * 0.5f
            val textY = height.toFloat() * percentageProgress - 2 * paddingEnd * percentageProgress
            textPaint.getTextBounds(text, 0, text.length, textRect)
            textPaint.color = if (textY < bottomBorderY) {
                Color.WHITE
            } else {
                Color.BLACK
            }
            canvas?.translate(
                textY,
                textX - textRect.exactCenterX()
            )
            canvas?.rotate(90f)
            canvas?.drawText(text, 0f, 0f, textPaint)
            canvas?.restore()
        }
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