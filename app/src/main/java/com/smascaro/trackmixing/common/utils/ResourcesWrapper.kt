package com.smascaro.trackmixing.common.utils

import android.content.Context
import javax.inject.Inject

class ResourcesWrapper @Inject constructor(private val context: Context) {
    fun getString(id: Int): String {
        return context.resources.getString(id)
    }

    fun getDimension(id: Int): Float {
        return context.resources.getDimension(id)
    }

    fun getFloat(id: Int): Float {
        return context.resources.getFloat(id)
    }

    fun getInteger(id: Int): Int {
        return context.resources.getInteger(id)
    }

}