package com.smascaro.trackmixing.common.utils

import org.junit.Test

internal class TrackVolumeBundleTest {

    @Test
    fun bundleAsString() {
        val bundle = TrackVolumeBundle(50, 50, 100, 100)
        val stringifiedBundle = bundle.bundleAsString()
        assert(stringifiedBundle.isNotEmpty())
    }

    @Test
    fun parse() {
        val bundle = TrackVolumeBundle(50, 50, 100, 100)
        val stringifiedBundle = bundle.bundleAsString()
        val parsedBundle = TrackVolumeBundle.parse(stringifiedBundle)
        assert(parsedBundle.vocals == 50)
        assert(parsedBundle.other == 50)
        assert(parsedBundle.bass == 100)
        assert(parsedBundle.drums == 100)
    }
}