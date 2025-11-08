package com.memowave.app.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Stateless UI for the bottom navigation bar.
 *
 * @param selectedDestination Index of the selected tab
 * @param onDestinationSelected Callback when a tab is selected
 */
@Composable
fun BottomNavigationBarContent(
    selectedDestination: Int,
    onDestinationSelected: (Screen) -> Unit
) {
    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
        Screen.navBarScreens.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == index,
                onClick = {
                    if (selectedDestination != index) {
                        onDestinationSelected(destination)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconResId!!),
                        contentDescription = when (destination) {
                            is Screen.MainPage -> "Home icon"
                            is Screen.Library -> "Library icon"
                            is Screen.Games -> "Games icon"
                            is Screen.Profile -> "Profile icon"
                            else -> null
                        }
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = requireNotNull(destination.labelResId) { "labelResId must not be null for navBarScreens" })
                    )
                }
            )
        }
    }
}

/**
 * Navigation bar with navigation logic (NavController).
 *
 * @param selectedDestination Index of the selected tab
 * @param navController NavController for navigation
 */
@Composable
fun BottomNavigationBar(
    selectedDestination: Int,
    navController: NavController
) {
    BottomNavigationBarContent(
        selectedDestination = selectedDestination,
        onDestinationSelected = { destination ->
            navController.navigate(route = destination.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}

/**
 * Preview for BottomNavigationBarContent (stateless, no NavController).
 */
@Preview
@Composable
fun BottomNavigationBarPreview() {
    MemowaveTheme {
        BottomNavigationBarContent(selectedDestination = 0, onDestinationSelected = { _ -> })
    }
}