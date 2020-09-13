package com.smascaro.trackmixing.settings.business.downloadtestdata.usecase.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TestDataFilesApi {
    @GET("{file}")
    fun downloadTestItemBundle(@Path("file") zipFileName: String): Call<ResponseBody>
}