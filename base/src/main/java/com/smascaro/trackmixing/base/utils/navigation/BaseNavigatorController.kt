package com.smascaro.trackmixing.base.utils.navigation

import androidx.navigation.NavController
import com.smascaro.trackmixing.base.ui.architecture.controller.BaseController
import com.smascaro.trackmixing.base.ui.architecture.view.ViewMvc

abstract class BaseNavigatorController<VIEW_MVC : ViewMvc>(protected val navigationHelper: NavigationHelper) :
    BaseController<VIEW_MVC>() {
    fun bindNavController(navController: NavController) {
        navigationHelper.bindNavController(navController)
    }
}