package com.smascaro.trackmixing.settings.business.downloadtestdata.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.smascaro.trackmixing.R

class ConfirmDownloadTestDataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.fragment_confirm_download_test_data, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnConfirmationOk =
            view.findViewById<MaterialButton>(R.id.btn_confirm_download_test_data_ok)
        btnConfirmationOk.setOnClickListener {
            Toast.makeText(context, "Start downloading files", Toast.LENGTH_SHORT).show()
            findNavController().navigate(ConfirmDownloadTestDataFragmentDirections.actionConfirmDownloadTestDataFragmentToNextStepWithTestDataFragment())
        }
        val btnConfirmationCancel =
            view.findViewById<MaterialButton>(R.id.btn_confirm_download_test_data_cancel)
        btnConfirmationCancel.setOnClickListener {
            activity?.finish()
        }
    }
}