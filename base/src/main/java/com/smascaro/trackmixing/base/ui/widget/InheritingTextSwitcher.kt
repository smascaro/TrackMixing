package com.smascaro.trackmixing.base.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.view.children
import com.smascaro.trackmixing.base.R
import timber.log.Timber

class InheritingTextSwitcher(context: Context, attrs: AttributeSet?) :
    TextSwitcher(context, attrs) {
    var _childrenTextSize: Float
    var _childrenTextColor: Int
    var childrenTextSize: Float
        get() {
            return (children.first() as TextView).textSize
        }
        set(value) {
            if (value != _childrenTextSize) {
                children.forEach {
                    (it as TextView).textSize = value
                }
                _childrenTextSize = value
            }
        }
    var childrenTextColor: Int
        get() {
            return _childrenTextColor
        }
        set(value) {
            if (value != _childrenTextColor) {
                children.forEach {
                    (it as TextView).setTextColor(value)
                }
                _childrenTextColor = value
            }
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.InheritingTextSwitcher, 0, 0
        ).apply {
            _childrenTextSize =
                getDimension(R.styleable.InheritingTextSwitcher_childrenTextSize, 14f)
            _childrenTextColor =
                getColor(R.styleable.InheritingTextSwitcher_childrenTextColor, Color.BLACK)
        }
        Timber.d("InheritingTextSwitcher init method")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        children.forEach {
            if (it is TextView) {
                it.textSize = _childrenTextSize
                it.setTextColor(_childrenTextColor)
            }
        }
    }
}