package com.smascaro.trackmixing.di.settings

import androidx.lifecycle.ViewModel
import com.smascaro.trackmixing.di.ViewModelKey
import com.smascaro.trackmixing.settings.testdata.selection.view.TestDataViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SettingsViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(TestDataViewModel::class)
    abstract fun bindTestDataViewModel(testDataViewModel: TestDataViewModel): ViewModel
}