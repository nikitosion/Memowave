package com.memowave.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.memowave.app.AppViewModel
import com.memowave.app.ui.common.BottomNavigationBar
import com.memowave.app.ui.navigation.NavGraph
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.theme.MemowaveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Memowave()
        }
    }
}

@Composable
fun Memowave(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val navBarScreens = Screen.navBarScreens
    val showNavBar =
        currentRoute?.let { route -> navBarScreens.any { it.route == route } } ?: false
    val selectedDestination = navBarScreens.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    MemowaveTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                if (showNavBar) {
                    BottomNavigationBar(
                        selectedDestination = selectedDestination,
                        navController = navController
                    )
                }
            },
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
                ,
            ) {
                NavGraph(navController, appViewModel)
            }
        }
    }
}