package com.smascaro.trackmixing.details.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.view.ui.BaseFragment
import com.smascaro.trackmixing.details.controller.TrackDetailsController
import com.smascaro.trackmixing.main.view.MainActivity
import com.smascaro.trackmixing.ui.details.TrackDetailsFragmentArgs
import javax.inject.Inject

class TrackDetailsFragment : BaseFragment() {

    private val args: TrackDetailsFragmentArgs by navArgs()

    @Inject
    lateinit var controller: TrackDetailsController

    @Inject
    lateinit var viewMvc: TrackDetailsViewMvc

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = MaterialContainerTransform().apply {
//            drawingViewId=R.id.nav_host_fragment
//            duration = 3000
//            fadeMode=MaterialContainerTransform.FADE_MODE_THROUGH
//            interpolator = FastOutSlowInInterpolator()
//        }
//        sharedElementEnterTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_START).apply {
//            duration=300
//            interpolator=FastOutSlowInInterpolator()
//        }
//        sharedElementReturnTransition = DraweeTransition.createTransitionSet(
//            ScalingUtils.ScaleType.FIT_CENTER,
//            ScalingUtils.ScaleType.CENTER_CROP
//        ).apply { duration = 5000 }
//        postponeEnterTransition()
        sharedElementEnterTransition = buildContainerTransform()
        sharedElementReturnTransition = buildContainerTransform()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewMvc.bindRootView(inflater.inflate(R.layout.fragment_track_details, null, false))
        viewMvc.bindTrack(args.track)
        controller.bindView(viewMvc)

        controller.initUI()
        return viewMvc.getRootView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()
    }

    override fun onStart() {
        super.onStart()
        controller.onStart()
        Toast.makeText(context, "Video key: ${args.track.videoKey}", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        controller.onStop()
    }

    private fun buildContainerTransform(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            duration = 300
        }
    }

}
