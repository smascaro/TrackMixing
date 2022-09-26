package com.smascaro.trackmixing.base.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import javax.inject.Inject

class ResourcesWrapper @Inject constructor(private val context: Context) {
    fun getString(id: Int, vararg args: Any): String {
        return context.resources.getString(id, *args)
    }

    fun getDimension(id: Int): Float {
        return context.resources.getDimension(id)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFloat(id: Int): Float {
        return context.resources.getFloat(id)
    }

    fun getInteger(id: Int): Int {
        return context.resources.getInteger(id)
    }

    fun getLong(id: Int): Long {
        return getInteger(id).toLong()
    }

    fun getColor(id: Int): Int {
        return ContextCompat.getColor(context, id)
    }
}