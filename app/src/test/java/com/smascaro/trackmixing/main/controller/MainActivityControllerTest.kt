package com.smascaro.trackmixing.main.controller

import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.main.view.MainActivityViewMvc
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainActivityControllerTest {
    private lateinit var SUT: MainActivityController

    // region constants

    // endregion constants

    // region helper fields
    @Mock lateinit var navigationHelper: NavigationHelper

    @Mock lateinit var viewMvc: MainActivityViewMvc
    // endregion helper fields

    @Before
    fun setup() {
        SUT = MainActivityController(navigationHelper)
        SUT.bindViewMvc(viewMvc)
    }

    // region tests
    @Test
    fun onStart_registersListener() {
        // Arrange
        // Act
        SUT.onStart()
        // Assert
        verify(viewMvc).registerListener(SUT)
    }

    @Test
    fun onStop_unregistersListener() {
        // Arrange
        // Act
        SUT.onStop()
        // Assert
        verify(viewMvc).unregisterListener(SUT)
    }

    @Test
    fun updateTitle_toolbarTitleIsUpdated() {
        // Arrange
        val title = "Test title"
        val enableBackNavigation = true
        // Act
        SUT.updateTitle(title, enableBackNavigation)
        // Assert
        verify(viewMvc).updateTitle(title, enableBackNavigation)
    }

    @Test
    fun onToolbarBackButtonPressed_viewIsCleanedUp() {
        // Arrange
        // Act
        SUT.onToolbarBackButtonPressed()
        // Assert
        verify(viewMvc).cleanUp()
    }

    @Test
    fun onToolbarBackButtonPressed_navigatesBack() {
        // Arrange
        // Act
        SUT.onToolbarBackButtonPressed()
        // Assert
        verify(navigationHelper).back()
    }
    // endregion tests

    // region helper methods

    // endregion helper methods

    // region helper classes
    // endregion helper classes
}