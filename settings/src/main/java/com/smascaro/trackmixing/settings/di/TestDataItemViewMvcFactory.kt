package com.smascaro.trackmixing.settings.di

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.base.di.ItemViewMvcFactory
import com.smascaro.trackmixing.settings.testdata.selection.view.testdataitem.SelectTestDataItemViewMvc
import com.smascaro.trackmixing.settings.testdata.selection.view.testdataitem.SelectTestDataItemViewMvcImpl
import javax.inject.Inject

class TestDataItemViewMvcFactory @Inject constructor(
    private val glide: RequestManager
) : ItemViewMvcFactory<SelectTestDataItemViewMvc> {
    override fun create(): SelectTestDataItemViewMvc {
        return SelectTestDataItemViewMvcImpl(
            glide
        )
    }
}