package com.smascaro.trackmixing.base.network.testdata.api

import com.smascaro.trackmixing.base.network.testdata.model.TestDataBundleInfoResponseSchema
import retrofit2.http.GET
import retrofit2.http.Path

interface TestDataApi {
    @GET("{file}")
    suspend fun downloadTestDataBundleFile(@Path("file") fileName: String): TestDataBundleInfoResponseSchema
}