package com.smascaro.trackmixing.common.controller

import androidx.navigation.NavController
import com.smascaro.trackmixing.common.utils.NavigationHelper
import com.smascaro.trackmixing.common.view.architecture.ViewMvc

abstract class BaseNavigatorController<VIEW_MVC : ViewMvc>(protected val navigationHelper: NavigationHelper) :
    BaseController<VIEW_MVC>() {
    fun bindNavController(navController: NavController) {
        navigationHelper.bindNavController(navController)
    }
}