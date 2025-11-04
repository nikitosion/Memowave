package com.memowave.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.memowave.app.ui.screen.authentification.AuthViewModel
import com.memowave.app.ui.screen.authentification.LoginRoute
import com.memowave.app.ui.screen.main_page.MainPageRoute

sealed class Screen(val route: String) {
    object MainPage : Screen("main_page")
    object Login : Screen("login")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.MainPage.route) {
            MainPageRoute()
        }
        composable(Screen.Login.route) {
            val authViewModel = hiltViewModel<AuthViewModel>()
            LoginRoute(authViewModel = authViewModel, navController = navController)
        }
    }
}