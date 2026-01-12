package com.memowave.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.memowave.app.R
import com.memowave.app.ui.screen.authentification.AuthViewModel
import com.memowave.app.ui.screen.authentification.ForgotPasswordRoute
import com.memowave.app.ui.screen.authentification.LoginRoute
import com.memowave.app.ui.screen.authentification.ResetPasswordRoute
import com.memowave.app.ui.screen.authentification.SignUpRoute
import com.memowave.app.ui.screen.main_page.MainPageRoute
import com.memowave.app.ui.screen.profile.ProfileRoute

sealed class Screen(
    val route: String,
    val iconResId: Int? = null,
    val labelResId: Int? = null,
    val showInAppBar: Boolean = false
) {
    object MainPage : Screen(
        route = "main_page",
        iconResId = R.drawable.round_home_24,
        labelResId = R.string.nav_bar_main_page,
        showInAppBar = true
    )

    object Library : Screen(
        route = "library",
        iconResId = R.drawable.round_local_library_24,
        labelResId = R.string.nav_bar_library,
        showInAppBar = true
    )

    object Games : Screen(
        route = "games",
        iconResId = R.drawable.round_toys_and_games_24,
        labelResId = R.string.nav_bar_games,
        showInAppBar = true
    )

    object Profile : Screen(
        route = "profile",
        iconResId = R.drawable.round_person_24,
        labelResId = R.string.nav_bar_profile,
        showInAppBar = true
    )

    object Login : Screen(route = "login")
    object ForgotPassword : Screen(route = "forgot_password")
    object SignUp : Screen(route = "sign_up")
    object ResetPassword : Screen(route = "reset_password")

    companion object {
        val allScreens =
            listOf(MainPage, Library, Games, Profile, Login, ForgotPassword, SignUp, ResetPassword)
        val navBarScreens = allScreens.filter { it.showInAppBar }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.MainPage.route) {
            MainPageRoute()
        }
        composable(Screen.Library.route) {
            // LibraryRoute()
        }
        composable(Screen.Games.route) {
            // LibraryRoute()
        }
        composable(Screen.Profile.route) {
            ProfileRoute()
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