package com.smascaro.trackmixing.di.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smascaro.trackmixing.settings.testdata.selection.view.TestDataViewModel
import javax.inject.Inject
import javax.inject.Provider

class TestDataViewModelFactory @Inject constructor(
    viewModelProvider: Provider<TestDataViewModel>
) : ViewModelProvider.Factory {
    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        TestDataViewModel::class.java to viewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}