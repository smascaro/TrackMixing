package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data

import com.smascaro.trackmixing.settings.business.downloadtestdata.selection.model.TestDataBundleInfoResponseSchema
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TestDataApi {
    @GET("{file}")
    fun downloadTestDataBundleFile(@Path("file") fileName: String): Call<TestDataBundleInfoResponseSchema>
}