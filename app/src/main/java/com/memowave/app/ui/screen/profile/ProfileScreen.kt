package com.memowave.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.streak.StreakState
import com.memowave.app.domain.model.user.User
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.main_page.components.StreakCard
import com.memowave.app.ui.screen.settings.components.ProfileHeaderCard
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun ProfileRoute(navController: NavController, profileViewModel: ProfileViewModel) {
    ProfileScreen(navController, profileViewModel)
}

@Composable
fun ProfileScreen(navController: NavController, profileViewModel: ProfileViewModel) {
    val uiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    ProfileScreenContent(
        user = uiState.user,
        streak = uiState.streak,
        onSettingsClick = { navController.navigate(Screen.AppSettings.route) },
        onProfileHeaderClick = { navController.navigate(Screen.SettingsPersonal.route) }
    )
}

@Composable
fun ProfileScreenContent(
    user: User,
    streak: StreakState,
    onSettingsClick: () -> Unit,
    onProfileHeaderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp, bottom = 32.dp)
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.End)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            onClick = onSettingsClick,
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.round_settings_24),
                contentDescription = "Settings button",
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        ProfileHeaderCard(
            modifier = Modifier.padding(top = 20.dp),
            user = user,
            onClick = onProfileHeaderClick
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            text = stringResource(R.string.profile_experience, user.experience),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        StreakCard(
            state = streak,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    MemowaveTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
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
                ProfileScreenContent(
                    user = User(
                        username = "Albert",
                        email = "albert@example.com",
                        experience = 1234,
                    ),
                    streak = StreakState(
                        currentStreak = 7,
                        longestStreak = 12,
                        dailyTarget = 10,
                        wordsCompletedToday = 4,
                        isAlive = true,
                    ),
                    onSettingsClick = {},
                    onProfileHeaderClick = {},
                )
            }
        }
    }
}
