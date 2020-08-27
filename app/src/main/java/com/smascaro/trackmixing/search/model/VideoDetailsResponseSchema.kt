package com.smascaro.trackmixing.search.model

data class VideoDetailsResponseSchema(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val pageInfo: PageInfo
) {
    data class Item(
        val contentDetails: ContentDetails,
        val etag: String,
        val id: String,
        val kind: String
    ) {
        data class ContentDetails(
            val caption: String,
            val contentRating: ContentRating,
            val definition: String,
            val dimension: String,
            val duration: String,
            val licensedContent: Boolean,
            val projection: String
        ) {
            class ContentRating
        }
    }

    data class PageInfo(
        val resultsPerPage: Int,
        val totalResults: Int
    )
}