package com.smascaro.trackmixing.ui.details

import android.graphics.Color
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.ui.common.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrackDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackDetailsFragment : BaseFragment() {

    private val args: TrackDetailsFragmentArgs by navArgs()
    private var mTrack = null
    private lateinit var mTrackDetailsController: TrackDetailsController
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
        val viewMvc =
            getCompositionRoot().getViewMvcFactory().getTrackDetailsViewMvc(null, args.track)
        mTrackDetailsController =
            getCompositionRoot().getTrackDetailsController(getNavigationController())
        mTrackDetailsController.bindView(viewMvc)
        mTrackDetailsController.initUI()
        return viewMvc.getRootView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startPostponedEnterTransition()
    }

    override fun onStart() {
        super.onStart()
        mTrackDetailsController.onStart()
        Toast.makeText(context, "Video key: ${args.track.videoKey}", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        mTrackDetailsController.onStop()
    }

    private fun buildContainerTransform(): MaterialContainerTransform {
        return MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            duration = 2000
        }
    }

}
