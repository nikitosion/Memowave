package com.memowave.app.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.user.User
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.settings.components.ProfileHeaderCard
import com.memowave.app.ui.screen.settings.components.SettingsGroup
import com.memowave.app.ui.screen.settings.components.SettingsNavItem
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun AppSettingsRoute(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    AppSettingsContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onNavigate = { route -> navController.navigate(route) },
        onLogoutRequest = viewModel::showLogoutDialog,
        onLogoutDismiss = viewModel::dismissLogoutDialog,
        onLogoutConfirm = viewModel::confirmLogout
    )
}

@Composable
private fun AppSettingsContent(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogoutRequest: () -> Unit,
    onLogoutDismiss: () -> Unit,
    onLogoutConfirm: () -> Unit
) {
    val fallbackUsername = stringResource(R.string.settings_user_unknown)

    SettingsScaffold(
        title = stringResource(R.string.settings_title),
        onBackClick = onBackClick,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SettingsGroup(
            title = stringResource(R.string.settings_section_account),
            items = listOf(
                SettingsNavItem(
                    title = stringResource(R.string.settings_personal),
                    iconRes = R.drawable.round_badge_24,
                    accent = AccentTone.PRIMARY,
                    onClick = { onNavigate(Screen.SettingsPersonal.route) }
                ),
                SettingsNavItem(
                    title = stringResource(R.string.settings_security),
                    iconRes = R.drawable.round_lock_24,
                    accent = AccentTone.TERTIARY,
                    onClick = { onNavigate(Screen.SettingsSecurity.route) }
                )
            )
        )

        SettingsGroup(
            title = stringResource(R.string.settings_section_learning),
            items = listOf(
                SettingsNavItem(
                    title = stringResource(R.string.settings_goals),
                    iconRes = R.drawable.round_stars_24,
                    accent = AccentTone.SECONDARY,
                    onClick = { onNavigate(Screen.SettingsGoals.route) }
                ),
                SettingsNavItem(
                    title = stringResource(R.string.settings_algorithm),
                    iconRes = R.drawable.round_calculate_24,
                    accent = AccentTone.SECONDARY,
                    onClick = { onNavigate(Screen.SettingsAlgorithm.route) }
                )
            )
        )

        SettingsGroup(
            title = stringResource(R.string.settings_section_app),
            items = listOf(
                SettingsNavItem(
                    title = stringResource(R.string.settings_appearance),
                    iconRes = R.drawable.round_palette_24,
                    accent = AccentTone.WARNING,
                    onClick = { onNavigate(Screen.SettingsAppearance.route) }
                ),
                SettingsNavItem(
                    title = stringResource(R.string.settings_notifications),
                    iconRes = R.drawable.round_notifications_24,
                    accent = AccentTone.SECONDARY,
                    onClick = { onNavigate(Screen.SettingsNotifications.route) }
                ),
                SettingsNavItem(
                    title = stringResource(R.string.settings_about),
                    iconRes = R.drawable.round_info_24,
                    accent = AccentTone.NEUTRAL,
                    onClick = { onNavigate(Screen.SettingsAbout.route) }
                ),
                SettingsNavItem(
                    title = stringResource(R.string.settings_report_bug),
                    iconRes = R.drawable.round_bug_report_24,
                    accent = AccentTone.ERROR,
                    onClick = { onNavigate(Screen.SettingsReportBug.route) }
                )
            )
        )

        LogoutCard(onClick = onLogoutRequest)
    }

    if (uiState.logoutDialogShown) {
        AlertDialog(
            onDismissRequest = onLogoutDismiss,
            title = { Text(stringResource(R.string.settings_logout_confirm_title)) },
            text = { Text(stringResource(R.string.settings_logout_confirm_message)) },
            confirmButton = {
                TextButton(onClick = onLogoutConfirm) {
                    Text(
                        stringResource(R.string.settings_logout_confirm_action),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onLogoutDismiss) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Settings hub — light")
@Composable
private fun AppSettingsContentPreview() {
    MemowaveTheme {
        AppSettingsContent(
            uiState = SettingsUiState(
                user = User(id = 1, username = "Nikita", email = "nikita@example.com")
            ),
            onBackClick = {},
            onNavigate = {},
            onLogoutRequest = {},
            onLogoutDismiss = {},
            onLogoutConfirm = {}
        )
    }
}

@Composable
private fun LogoutCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(R.drawable.round_logout_24),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.settings_logout),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
