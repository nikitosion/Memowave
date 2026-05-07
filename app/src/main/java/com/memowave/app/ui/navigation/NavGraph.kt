package com.memowave.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.memowave.app.AppViewModel
import com.memowave.app.R
import com.memowave.app.core.auth.AuthState
import com.memowave.app.ui.screen.authentification.ForgotPasswordRoute
import com.memowave.app.ui.screen.authentification.ForgotPasswordViewModel
import com.memowave.app.ui.screen.authentification.LoginRoute
import com.memowave.app.ui.screen.authentification.LoginViewModel
import com.memowave.app.ui.screen.authentification.ResetPasswordRoute
import com.memowave.app.ui.screen.authentification.ResetPasswordViewModel
import com.memowave.app.ui.screen.authentification.SignUpRoute
import com.memowave.app.ui.screen.authentification.SignUpViewModel
import com.memowave.app.ui.screen.library.LibraryRoute
import com.memowave.app.ui.screen.main_page.MainPageRoute
import com.memowave.app.ui.screen.profile.ProfileRoute
import com.memowave.app.ui.screen.profile.ProfileViewModel
import com.memowave.app.ui.screen.flashcard.FlashcardRoute
import com.memowave.app.ui.screen.settings.AppSettingsRoute
import com.memowave.app.ui.screen.settings.about.AboutRoute
import com.memowave.app.ui.screen.settings.algorithm.AlgorithmRoute
import com.memowave.app.ui.screen.settings.appearance.AppearanceRoute
import com.memowave.app.ui.screen.settings.goals.GoalsRoute
import com.memowave.app.ui.screen.settings.notifications.NotificationsRoute
import com.memowave.app.ui.screen.settings.personal.PersonalInfoRoute
import com.memowave.app.ui.screen.settings.report_bug.ReportBugRoute
import com.memowave.app.ui.screen.settings.security.SecurityRoute
import com.memowave.app.ui.screen.splash.SplashRoute
import com.memowave.app.ui.screen.splash.SplashViewModel

sealed class Screen(
    val route: String,
    val iconResId: Int? = null,
    val labelResId: Int? = null,
    val showInAppBar: Boolean = false,
    val hasCustomTopBar: Boolean = false
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

    object Splash : Screen(route = "splash")
    object Login : Screen(route = "login")
    object ForgotPassword : Screen(route = "forgot_password")
    object SignUp : Screen(route = "sign_up")

    /**
     * Reset-password destination. The `userId` is passed as a path arg from
     * [ForgotPassword] via [com.memowave.app.ui.screen.authentification.helper.AuthNavEvent.ToResetPassword].
     */
    object ResetPassword : Screen(route = "reset_password/{userId}") {
        const val USER_ID_ARG = "userId"
    }
    object AppSettings : Screen(route = "app_settings", hasCustomTopBar = true)
    object SettingsPersonal : Screen(route = "app_settings/personal", hasCustomTopBar = true)
    object SettingsSecurity : Screen(route = "app_settings/security", hasCustomTopBar = true)
    object SettingsGoals : Screen(route = "app_settings/goals", hasCustomTopBar = true)
    object SettingsAlgorithm : Screen(route = "app_settings/algorithm", hasCustomTopBar = true)
    object SettingsAppearance : Screen(route = "app_settings/appearance", hasCustomTopBar = true)
    object SettingsNotifications : Screen(route = "app_settings/notifications", hasCustomTopBar = true)
    object SettingsAbout : Screen(route = "app_settings/about", hasCustomTopBar = true)
    object SettingsReportBug : Screen(route = "app_settings/report_bug", hasCustomTopBar = true)
    object Flashcard : Screen(route = "flashcard", hasCustomTopBar = true)

    companion object {
        val allScreens =
            listOf(
                MainPage, Library, Games, Profile,
                Splash, Login, ForgotPassword, SignUp, ResetPassword,
                AppSettings, SettingsPersonal, SettingsSecurity,
                SettingsGoals, SettingsAlgorithm, SettingsAppearance,
                SettingsNotifications, SettingsAbout, SettingsReportBug,
                Flashcard
            )
        val navBarScreens = allScreens.filter { it.showInAppBar }
    }
}

@Composable
fun NavGraph(navController: NavHostController, appViewModel: AppViewModel) {
    val authState by appViewModel.authStateManager.authState.collectAsState()

    val sessionExpiredMessage = stringResource(R.string.notification_session_expired)
    val logoutSuccessMessage = stringResource(R.string.notification_logout_success)

    var isFirstLaunch by remember { mutableStateOf(true) }

    // Drive logout-side navigation/notifications from the auth state. The
    // login-side ("welcome back" / "registration success") is owned by the
    // individual auth screens to avoid the cross-source notification race
    // that would otherwise pit a screen-specific message against a generic one.
    LaunchedEffect(authState) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            return@LaunchedEffect
        }

        val state = authState
        if (state is AuthState.Unauthenticated) {
            when (state.reason) {
                AuthState.Unauthenticated.Reason.SessionExpired ->
                    appViewModel.notificationManager.showError(sessionExpiredMessage)
                AuthState.Unauthenticated.Reason.ManualLogout ->
                    appViewModel.notificationManager.showSuccess(logoutSuccessMessage)
                AuthState.Unauthenticated.Reason.Initial -> { /* silent */ }
            }

            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            val viewModel = hiltViewModel<SplashViewModel>()
            SplashRoute(viewModel = viewModel, navController = navController)
        }
        composable(Screen.MainPage.route) {
            MainPageRoute(appViewModel, navController)
        }
        composable(Screen.Library.route) {
            LibraryRoute(
                navController = navController
            )
        }
        composable(Screen.Games.route) {
            // LibraryRoute()
        }
        composable(Screen.Profile.route) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            ProfileRoute(navController, profileViewModel)
        }
        composable(Screen.AppSettings.route) {
            AppSettingsRoute(navController = navController)
        }
        composable(Screen.SettingsPersonal.route) {
            PersonalInfoRoute(navController = navController)
        }
        composable(Screen.SettingsSecurity.route) {
            SecurityRoute(navController = navController)
        }
        composable(Screen.SettingsGoals.route) {
            GoalsRoute(navController = navController)
        }
        composable(Screen.SettingsAlgorithm.route) {
            AlgorithmRoute(navController = navController)
        }
        composable(Screen.SettingsAppearance.route) {
            AppearanceRoute(navController = navController)
        }
        composable(Screen.SettingsNotifications.route) {
            NotificationsRoute(navController = navController)
        }
        composable(Screen.SettingsAbout.route) {
            AboutRoute(navController = navController)
        }
        composable(Screen.SettingsReportBug.route) {
            ReportBugRoute(navController = navController)
        }

        composable(Screen.Flashcard.route) {
            FlashcardRoute(appViewModel, navController)
        }

        composable(Screen.Login.route) {
            val viewModel = hiltViewModel<LoginViewModel>()
            LoginRoute(viewModel = viewModel, navController = navController)
        }

        composable(Screen.ForgotPassword.route) {
            val viewModel = hiltViewModel<ForgotPasswordViewModel>()
            ForgotPasswordRoute(viewModel = viewModel, navController = navController)
        }

        composable(Screen.SignUp.route) {
            val viewModel = hiltViewModel<SignUpViewModel>()
            SignUpRoute(viewModel = viewModel, navController = navController)
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument(Screen.ResetPassword.USER_ID_ARG) { type = NavType.LongType }
            )
        ) {
            val viewModel = hiltViewModel<ResetPasswordViewModel>()
            ResetPasswordRoute(viewModel = viewModel, navController = navController)
        }
    }
}