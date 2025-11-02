package com.memowave.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.memowave.app.ui.screen.main_page.MainPageRoute

sealed class Screen(val route: String) {
    object MainPage : Screen("main_page")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.MainPage.route) {
        composable(Screen.MainPage.route) {
            MainPageRoute()
        }
    }
}