package com.smascaro.trackmixing.search.model.repository.datasource

import com.smascaro.trackmixing.R
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.search.model.SearchResult
import com.smascaro.trackmixing.search.model.SearchResultResponseSchema
import com.smascaro.trackmixing.search.model.VideoDetailsResponseSchema
import com.smascaro.trackmixing.search.model.repository.YoutubeApi
import com.smascaro.trackmixing.search.model.repository.events.SearchResultsObtainedEvent
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class SearchResultsNetworkDataSource @Inject constructor(
    private val youtubeApi: YoutubeApi,
    resourcesWrapper: ResourcesWrapper
) {
    private val apiKey = resourcesWrapper.getString(R.string.youtube_api_key)

    fun queryAndNotify(query: String) {
        youtubeApi.search(query, apiKey).enqueue(object : Callback<SearchResultResponseSchema> {
            override fun onFailure(call: Call<SearchResultResponseSchema>, t: Throwable) {
                EventBus.getDefault()
                    .post(SearchResultsObtainedEvent.Failure(t.message ?: "Unknown error"))
            }

            override fun onResponse(
                call: Call<SearchResultResponseSchema>,
                response: Response<SearchResultResponseSchema>
            ) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Timber.d("Results from Youtube API:search: $responseBody")
                    fetchDetails(responseBody)
                }
            }
        })
    }

    private fun fetchDetails(searchResultResponseSchema: SearchResultResponseSchema) {
        val searchResultIds = searchResultResponseSchema.items.map {
            it.id.videoId
        }.joinToString(separator = ",", prefix = "", postfix = "")
        youtubeApi.getDetails(searchResultIds, apiKey)
            .enqueue(object : Callback<VideoDetailsResponseSchema> {
                override fun onFailure(call: Call<VideoDetailsResponseSchema>, t: Throwable) {
                    EventBus.getDefault()
                        .post(SearchResultsObtainedEvent.Failure(t.message ?: "Unknown error"))
                }

                override fun onResponse(
                    call: Call<VideoDetailsResponseSchema>,
                    response: Response<VideoDetailsResponseSchema>
                ) {
                    val responseBody = response.body()
                    val searchResults = mutableListOf<SearchResult>()
                    if (responseBody != null) {
                        Timber.d("Results from Youtube API:list: $responseBody")
                        searchResultResponseSchema.items.forEach { searchResult ->
                            val found = responseBody.items.find { details ->
                                searchResult.id.videoId == details.id
                            }
                            if (found != null) {
                                searchResults.add(
                                    SearchResultMapper.map(searchResult, found)
                                )
                            }
                        }

                    }
                    EventBus.getDefault().post(SearchResultsObtainedEvent.Success(searchResults))
                }
            })
    }
}