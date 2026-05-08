package com.memowave.app.ui.screen.splash

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless splash UI: branded logo + centered loading indicator. Shown while
 * [SplashViewModel] performs the auto-login probe at app cold start.
 */
@Composable
fun SplashScreenContent() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MemowaveLogoColored(size = 96.dp)
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 32.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/**
 * Splash screen with ViewModel and one-shot navigation handling.
 *
 * Disables back press while the probe is in flight — the user shouldn't be able
 * to leave the splash before a routing decision is made.
 */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    navController: NavController,
) {
    BackHandler { /* no-op: prevent back press during the auto-login probe */ }

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is SplashNavEvent.ToMain -> navController.navigate(Screen.MainPage.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }

                is SplashNavEvent.ToLogin -> navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    SplashScreenContent()
}

/** Navigation wrapper for the Splash screen, matching the project's Route pattern. */
@Composable
fun SplashRoute(viewModel: SplashViewModel, navController: NavController) {
    SplashScreen(viewModel = viewModel, navController = navController)
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    MemowaveTheme {
        SplashScreenContent()
    }
}
