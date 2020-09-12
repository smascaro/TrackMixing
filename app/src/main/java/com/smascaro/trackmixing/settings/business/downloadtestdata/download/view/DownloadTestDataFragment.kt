package com.smascaro.trackmixing.settings.business.downloadtestdata.download.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.smascaro.trackmixing.R

class DownloadTestDataFragment : Fragment() {

    private val arguments: DownloadTestDataFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_download_test_data, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(
            context,
            "Downloading ${arguments.dataToDownload.size} items",
            Toast.LENGTH_SHORT
        ).show()
    }
}