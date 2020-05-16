package com.smascaro.trackmixing.ui.common

import com.smascaro.trackmixing.common.CustomApplication
import com.smascaro.trackmixing.common.di.CompositionRoot
import com.smascaro.trackmixing.common.di.ControllerCompositionRoot
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    private var mControllerCompositionRoot: ControllerCompositionRoot? = null

    protected fun getCompositionRoot(): ControllerCompositionRoot {
        if (mControllerCompositionRoot == null) {
            return ControllerCompositionRoot(
                (requireActivity().application as CustomApplication).getCompositionRoot(),
                requireActivity()
            )
        }
        return mControllerCompositionRoot!!
    }
}