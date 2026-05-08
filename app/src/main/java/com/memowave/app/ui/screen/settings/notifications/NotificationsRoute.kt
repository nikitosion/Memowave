package com.memowave.app.ui.screen.settings.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.SettingsRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.common.settings.SettingsToggleRow
import com.memowave.app.ui.screen.settings.about.AboutEntryPoint
import com.memowave.app.ui.screen.settings.notifications.components.PermissionRationaleCard
import com.memowave.app.ui.screen.settings.notifications.components.TimeBlock
import com.memowave.app.ui.theme.MemowaveTheme
import dagger.hilt.android.EntryPointAccessors

@Composable
fun NotificationsRoute(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val notificationManager: NotificationManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AboutEntryPoint::class.java
        ).notificationManager()
    }
    val comingSoon = stringResource(R.string.coming_soon_notifications)
    val showComingSoon: () -> Unit = { notificationManager.showInfo(comingSoon) }

    // Все колбэки временно перенаправлены в тост — модуль уведомлений будет
    // реализован позже. Состояние тогглов читается из DataStore только для
    // отображения, реальные записи отключены.
    NotificationsContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onMasterToggle = { showComingSoon() },
        onReminderTimeClick = showComingSoon,
        onStudyToggle = { showComingSoon() },
        onStreakRiskToggle = { showComingSoon() },
        onMilestonesToggle = { showComingSoon() },
        onQuietHoursToggle = { showComingSoon() },
        onQuietStartChange = { _, _ -> showComingSoon() },
        onQuietEndChange = { _, _ -> showComingSoon() }
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
