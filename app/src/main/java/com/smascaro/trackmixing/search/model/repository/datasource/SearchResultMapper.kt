package com.smascaro.trackmixing.search.model.repository.datasource

import com.smascaro.trackmixing.base.network.youtube.model.SearchResultResponseSchema
import com.smascaro.trackmixing.base.network.youtube.model.VideoDetailsResponseSchema
import com.smascaro.trackmixing.search.model.SearchResult

class SearchResultMapper {
    companion object {
        fun map(
            searchResultResponseSchemaItem: SearchResultResponseSchema.Item,
            videoDetailsResponseSchemaItem: VideoDetailsResponseSchema.Item
        ): SearchResult {
            return SearchResult(
                searchResultResponseSchemaItem.id.videoId,
                searchResultResponseSchemaItem.snippet.title,
                searchResultResponseSchemaItem.snippet.channelTitle,
                ISO8601Converter.parseDuration(videoDetailsResponseSchemaItem.contentDetails.duration)
                    .toInt(),
                searchResultResponseSchemaItem.snippet.thumbnails.high.url
            )
        }
    }
}