package com.memowave.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.memowave.app.AppViewModel
import com.memowave.app.core.media.ImageUrlResolver
import com.memowave.app.domain.repository.SettingsRepository
import com.memowave.app.ui.common.BottomNavigationBar
import com.memowave.app.ui.common.MemowaveTopBar
import com.memowave.app.ui.common.media.LocalImageUrlResolver
import com.memowave.app.ui.common.notification.NotificationHost
import com.memowave.app.ui.common.settings.LocalReduceMotion
import com.memowave.app.ui.navigation.NavGraph
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.theme.MemowaveTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageUrlResolver: ImageUrlResolver

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyPersistedLocale()
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalImageUrlResolver provides imageUrlResolver) {
                Memowave()
            }
        }
    }

    private fun applyPersistedLocale() {
        val saved = runBlocking { settingsRepository.getSettings().first() }.appLanguage
        val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val target = saved.orEmpty()
        if (current != target) {
            AppCompatDelegate.setApplicationLocales(
                if (target.isBlank()) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(target)
            )
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

    val currentScreen = Screen.allScreens.find { it.route == currentRoute }
    val canGoBack = navController.previousBackStackEntry != null
    val showDefaultTopBar = canGoBack && currentScreen?.hasCustomTopBar != true && !showNavBar

    val themeMode by appViewModel.themeMode.collectAsState()
    val reduceMotion by appViewModel.reduceMotion.collectAsState()

    MemowaveTheme(themeMode = themeMode) {
        CompositionLocalProvider(LocalReduceMotion provides reduceMotion) {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (showDefaultTopBar) {
                            MemowaveTopBar(
                                modifier = Modifier.statusBarsPadding(),
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    },
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
                                bottom = paddingValues.calculateBottomPadding()
                            ),
                    ) {
                        NavGraph(navController, appViewModel)
                    }

                    NotificationHost(
                        manager = appViewModel.notificationManager,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = paddingValues.calculateBottomPadding()),
                    )
                }
            }
        }
    }
}
