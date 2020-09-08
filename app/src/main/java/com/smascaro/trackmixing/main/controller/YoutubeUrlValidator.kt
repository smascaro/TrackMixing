package com.smascaro.trackmixing.main.controller

class YoutubeUrlValidator(private val url: String) {
    fun isValid(): Boolean {
        return url.contains(".youtube.") || url.contains("youtu.be")
    }
}