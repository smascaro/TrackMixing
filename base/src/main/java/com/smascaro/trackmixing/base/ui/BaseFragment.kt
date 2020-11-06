package com.smascaro.trackmixing.base.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import timber.log.Timber

abstract class BaseFragment : Fragment() {
    interface OnTitleChangeListener {
        fun changeTitle(title: String, enableBackNavigation: Boolean)
    }

    private var titleChangeListener: OnTitleChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            titleChangeListener = activity as OnTitleChangeListener
        } catch (e: ClassCastException) {
            Timber.w("Parent activity does not implement TitleChangeListener. Title won't be updated.")
        }
    }

    override fun onDetach() {
        super.onDetach()
        titleChangeListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (titleChangeListener != null) {
            titleChangeListener!!.changeTitle(getFragmentTitle(), isBackNavigable())
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    abstract fun getFragmentTitle(): String
    abstract fun isBackNavigable(): Boolean
}