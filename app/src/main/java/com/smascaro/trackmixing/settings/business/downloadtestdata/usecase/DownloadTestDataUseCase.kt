package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase

import com.google.gson.Gson
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfo
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfoResponseSchema
import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.toModelList
import com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data.TestDataApi
import okhttp3.ResponseBody
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
        testDataApi.downloadTestDataBundleFile("1dS1ioDF7k1jSsMnjBENKKDt69nB6PCZ5")
            .enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback(Result.Failure(t))
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected code $response")
                    }
                    val headers = response.headers()
                    response.body()?.let { body ->
                        val content = body.string()
                        Timber.d("Printing file content")
                        Timber.d(content)
                        val schema = Gson().fromJson(
                            content,
                            TestDataBundleInfoResponseSchema::class.java
                        )
                        val tracksList = schema.toModelList()
                        callback(Result.Success(tracksList))
                    }
                }

            })
    }
}