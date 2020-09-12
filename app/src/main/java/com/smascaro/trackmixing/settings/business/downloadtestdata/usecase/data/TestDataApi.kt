package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TestDataApi {
    @Streaming
    @GET("/uc?export=download")
    fun downloadTestDataBundleFile(@Query("id") resource: String): Call<ResponseBody>
}