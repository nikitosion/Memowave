package com.memowave.app.ui.screen.settings.notifications

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.SettingsRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.common.settings.SettingsToggleRow
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.settings.notifications.components.PermissionRationaleCard
import com.memowave.app.ui.screen.settings.notifications.components.TimeBlock
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun NotificationsRoute(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.setMasterEnabled(true)
        } else {
            viewModel.onPermissionDenied()
        }
    }

    NotificationsContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onMasterToggle = { enable ->
            if (enable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.setMasterEnabled(enable)
            }
        },
        onReminderTimeClick = { navController.navigate(Screen.SettingsGoals.route) },
        onStudyToggle = viewModel::setStudyReminders,
        onStreakRiskToggle = viewModel::setStreakRisk,
        onMilestonesToggle = viewModel::setMilestones,
        onQuietHoursToggle = viewModel::setQuietHoursEnabled,
        onQuietStartChange = viewModel::setQuietStart,
        onQuietEndChange = viewModel::setQuietEnd
    )
}

@Composable
private fun NotificationsContent(
    uiState: NotificationsUiState,
    onBackClick: () -> Unit,
    onMasterToggle: (Boolean) -> Unit,
    onReminderTimeClick: () -> Unit,
    onStudyToggle: (Boolean) -> Unit,
    onStreakRiskToggle: (Boolean) -> Unit,
    onMilestonesToggle: (Boolean) -> Unit,
    onQuietHoursToggle: (Boolean) -> Unit,
    onQuietStartChange: (Int, Int) -> Unit,
    onQuietEndChange: (Int, Int) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_notifications),
        onBackClick = onBackClick
    ) {
        if (uiState.permissionDenied) {
            PermissionRationaleCard()
        }

        SettingsSection(title = stringResource(R.string.settings_notifications_section_main)) {
            SettingsToggleRow(
                label = stringResource(R.string.settings_notifications_master),
                checked = uiState.masterEnabled,
                supportingText = stringResource(R.string.settings_notifications_master_supporting),
                onCheckedChange = onMasterToggle
            )
            SettingsRow(
                title = stringResource(R.string.settings_goals_reminder_time),
                supportingText = "%02d:%02d".format(uiState.reminderHour, uiState.reminderMinute),
                leadingIconRes = R.drawable.round_notifications_24,
                accent = AccentTone.SECONDARY,
                onClick = onReminderTimeClick,
                enabled = uiState.masterEnabled
            )
        }

        SettingsSection(title = stringResource(R.string.settings_notifications_section_types)) {
            SettingsToggleRow(
                label = stringResource(R.string.settings_notifications_study),
                checked = uiState.studyRemindersEnabled,
                onCheckedChange = onStudyToggle,
                enabled = uiState.masterEnabled,
                supportingText = stringResource(R.string.settings_notifications_study_supporting)
            )
            SettingsToggleRow(
                label = stringResource(R.string.settings_notifications_streak_risk),
                checked = uiState.streakRiskEnabled,
                onCheckedChange = onStreakRiskToggle,
                enabled = uiState.masterEnabled,
                supportingText = stringResource(R.string.settings_notifications_streak_risk_supporting)
            )
            SettingsToggleRow(
                label = stringResource(R.string.settings_notifications_milestones),
                checked = uiState.milestonesEnabled,
                onCheckedChange = onMilestonesToggle,
                enabled = uiState.masterEnabled,
                supportingText = stringResource(R.string.settings_notifications_milestones_supporting)
            )
        }

        SettingsSection(title = stringResource(R.string.settings_notifications_section_quiet)) {
            SettingsToggleRow(
                label = stringResource(R.string.settings_notifications_quiet_enable),
                checked = uiState.quietHoursEnabled,
                onCheckedChange = onQuietHoursToggle,
                enabled = uiState.masterEnabled,
                supportingText = stringResource(R.string.settings_notifications_quiet_supporting)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimeBlock(
                    label = stringResource(R.string.settings_notifications_quiet_from),
                    hour = uiState.quietStartHour,
                    minute = uiState.quietStartMinute,
                    onTimeChange = onQuietStartChange,
                    enabled = uiState.masterEnabled && uiState.quietHoursEnabled,
                    modifier = Modifier.weight(1f)
                )
                TimeBlock(
                    label = stringResource(R.string.settings_notifications_quiet_to),
                    hour = uiState.quietEndHour,
                    minute = uiState.quietEndMinute,
                    onTimeChange = onQuietEndChange,
                    enabled = uiState.masterEnabled && uiState.quietHoursEnabled,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Notifications — all on, quiet hours active")
@Composable
private fun NotificationsContentPreview() {
    MemowaveTheme {
        NotificationsContent(
            uiState = NotificationsUiState(
                masterEnabled = true,
                studyRemindersEnabled = true,
                streakRiskEnabled = true,
                milestonesEnabled = true,
                quietHoursEnabled = true,
                quietStartHour = 22,
                quietEndHour = 8,
                reminderHour = 19,
                reminderMinute = 30
            ),
            onBackClick = {},
            onMasterToggle = {},
            onReminderTimeClick = {},
            onStudyToggle = {},
            onStreakRiskToggle = {},
            onMilestonesToggle = {},
            onQuietHoursToggle = {},
            onQuietStartChange = { _, _ -> },
            onQuietEndChange = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Notifications — master off + permission denied")
@Composable
private fun NotificationsContentMasterOffPreview() {
    MemowaveTheme {
        NotificationsContent(
            uiState = NotificationsUiState(
                masterEnabled = false,
                permissionDenied = true
            ),
            onBackClick = {},
            onMasterToggle = {},
            onReminderTimeClick = {},
            onStudyToggle = {},
            onStreakRiskToggle = {},
            onMilestonesToggle = {},
            onQuietHoursToggle = {},
            onQuietStartChange = { _, _ -> },
            onQuietEndChange = { _, _ -> }
        )
    }
}
