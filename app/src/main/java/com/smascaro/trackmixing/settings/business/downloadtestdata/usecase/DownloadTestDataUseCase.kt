package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase

import com.smascaro.trackmixing.common.utils.AWS_S3_TEST_DATA_INFO_FILE_RESOURCE
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfoResponseSchema
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.toModelList
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataApi
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class DownloadTestDataUseCase @Inject constructor(
    private val testDataApi: TestDataApi
) {
    sealed class Result {
        class Success(val tracks: List<TestDataBundleInfo>) : Result()
        class Failure(val throwable: Throwable) : Result()
    }

    fun getTestDataBundleInfo(callback: (Result) -> Unit) {
        testDataApi.downloadTestDataBundleFile(AWS_S3_TEST_DATA_INFO_FILE_RESOURCE)
            .enqueue(object : retrofit2.Callback<TestDataBundleInfoResponseSchema> {
                override fun onFailure(
                    call: Call<TestDataBundleInfoResponseSchema>,
                    t: Throwable
                ) {
                    callback(Result.Failure(t))
                }

                override fun onResponse(
                    call: Call<TestDataBundleInfoResponseSchema>,
                    response: Response<TestDataBundleInfoResponseSchema>
                ) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    response.body()?.let { body ->
                        Timber.d(body.toString())
                        val tracksList = body.toModelList()
                        callback(Result.Success(tracksList))
                    }
                }

            })
    }
}