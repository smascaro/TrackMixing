package com.smascaro.trackmixing.main.controller

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class YoutubeUrlValidatorTest {
    private lateinit var SUT: YoutubeUrlValidator

    // region constants

    // endregion constants

    // region helper fields

    // endregion helper fields

    @Before
    fun setup() {
    }

    // region tests
    @Test
    fun isValid_shortenedUrl_trueReturned() {
        // Arrange
        val url = "https://youtu.be/PbEKIW3pUUk"
        SUT = YoutubeUrlValidator(url)
        // Act
        val isValid = SUT.isValid()
        // Assert
        Assert.assertTrue(isValid)
    }

    @Test
    fun isValid_fullUrl_trueReturned() {
        // Arrange
        val url = "https://www.youtube.com/watch?v=0vHdXPMFawQ&ab_channel=TAC12"
        SUT = YoutubeUrlValidator(url)
        // Act
        val isValid = SUT.isValid()
        // Assert
        Assert.assertTrue(isValid)
    }

    @Test
    fun isValid_mobileUrl_trueReturned() {
        // Arrange
        val url = "https://m.youtube.com/watch?feature=youtu.be&v=B7aaMrrMtUk"
        SUT = YoutubeUrlValidator(url)
        // Act
        val isValid = SUT.isValid()
        // Assert
        Assert.assertTrue(isValid)
    }

    @Test
    fun isValid_nonYoutubeUrl_falseReturned() {
        // Arrange
        val url =
            "https://stackoverflow.com/questions/14292863/how-to-mock-a-final-class-with-mockito"
        SUT = YoutubeUrlValidator(url)
        // Act
        val isValid = SUT.isValid()
        // Assert
        Assert.assertFalse(isValid)
    }

    @Test
    fun isValid_fakeYoutubeUrl_falseReturned() {
        // Arrange
        val url = "https://www.nottherealyoutube.com/watch?v=0vHdXPMFawQ&ab_channel=TAC12"
        SUT = YoutubeUrlValidator(url)
        // Act
        val isValid = SUT.isValid()
        // Assert
        Assert.assertFalse(isValid)
    }
    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes
}