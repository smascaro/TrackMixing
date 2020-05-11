package com.smascaro.trackmixing.ui.common

import com.smascaro.trackmixing.common.CustomApplication
import com.smascaro.trackmixing.common.di.CompositionRoot
import com.smascaro.trackmixing.common.di.ControllerCompositionRoot
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    private var mControllerCompositionRoot: ControllerCompositionRoot? = null

    protected fun getCompositionRoot(): ControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ControllerCompositionRoot(
                (application as CustomApplication).getCompositionRoot(),
                this
            )
        }
        return mControllerCompositionRoot!!
    }
}