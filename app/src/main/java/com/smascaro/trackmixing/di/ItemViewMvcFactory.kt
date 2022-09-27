package com.smascaro.trackmixing.di

import com.smascaro.trackmixing.base.ui.architecture.view.ViewMvc

interface ItemViewMvcFactory<T : ViewMvc> {
    fun create(): T
}