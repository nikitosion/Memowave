package com.memowave.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.memowave.app.ui.screen.authentification.AuthViewModel
import com.memowave.app.ui.screen.authentification.ForgotPasswordRoute
import com.memowave.app.ui.screen.authentification.LoginRoute
import com.memowave.app.ui.screen.authentification.ResetPasswordRoute
import com.memowave.app.ui.screen.authentification.SignUpRoute
import com.memowave.app.ui.screen.main_page.MainPageRoute

sealed class Screen(val route: String) {
    object MainPage : Screen("main_page")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object SignUp : Screen("sign_up")
    object ResetPassword : Screen("reset_password")
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.MainPage.route) {
            MainPageRoute()
        }
        composable(Screen.Login.route) {
            LoginRoute(authViewModel = authViewModel, navController = navController)
        }
        composable(Screen.ForgotPassword.route) {
            authViewModel.resetForgotPasswordState()
            ForgotPasswordRoute(authViewModel = authViewModel, navController = navController)
        }
        composable(Screen.SignUp.route) {
            authViewModel.resetSignUpState()
            SignUpRoute(authViewModel = authViewModel, navController = navController)
        }
        composable(Screen.ResetPassword.route) {
            authViewModel.resetResetPasswordState()
            ResetPasswordRoute(authViewModel = authViewModel, navController = navController)
        }
    }
}