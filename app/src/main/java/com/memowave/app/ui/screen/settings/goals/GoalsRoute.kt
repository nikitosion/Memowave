package com.memowave.app.ui.screen.settings.goals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.LabeledSlider
import com.memowave.app.ui.common.settings.SectionDivider
import com.memowave.app.ui.common.settings.SettingsItemColumn
import com.memowave.app.ui.common.settings.SettingsRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.settings.goals.components.DayOfWeekChips
import com.memowave.app.ui.screen.settings.goals.components.TimePickerRow
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun GoalsRoute(
    navController: NavController,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    GoalsContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onAlgorithmClick = { navController.navigate(Screen.SettingsAlgorithm.route) },
        onToggleDay = viewModel::onToggleDay,
        onReminderTimeChange = viewModel::onReminderTimeChange,
        onWeeklyGoalChange = { viewModel.onWeeklyGoalChange(it.toInt()) },
        onWeeklyGoalCommit = viewModel::onWeeklyGoalCommit,
        formatWords = { count -> context.getString(R.string.settings_goals_words_format, count) }
    )
}

@Composable
private fun GoalsContent(
    uiState: GoalsUiState,
    onBackClick: () -> Unit,
    onAlgorithmClick: () -> Unit,
    onToggleDay: (Int) -> Unit,
    onReminderTimeChange: (hour: Int, minute: Int) -> Unit,
    onWeeklyGoalChange: (Float) -> Unit,
    onWeeklyGoalCommit: () -> Unit,
    formatWords: (Int) -> String,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_goals),
        onBackClick = onBackClick
    ) {
        SettingsSection(title = stringResource(R.string.settings_goals_section_week)) {
            SettingsItemColumn(label = stringResource(R.string.settings_goals_days_label)) {
                DayOfWeekChips(
                    selected = uiState.goalDays,
                    onToggle = onToggleDay
                )
            }
            SectionDivider()
            TimePickerRow(
                title = stringResource(R.string.settings_goals_reminder_time),
                hour = uiState.reminderHour,
                minute = uiState.reminderMinute,
                onTimeChange = onReminderTimeChange,
                leadingIconRes = R.drawable.round_notifications_24,
                accent = AccentTone.SECONDARY
            )
            SectionDivider()
            LabeledSlider(
                title = stringResource(R.string.settings_goals_weekly_target),
                value = uiState.weeklyWordGoal.toFloat(),
                onValueChange = onWeeklyGoalChange,
                onValueChangeFinished = onWeeklyGoalCommit,
                valueRange = 5f..200f,
                steps = (200 - 5) / 5 - 1,
                valueFormatter = { formatWords(it.toInt()) }
            )
        }

        SettingsRow(
            title = stringResource(R.string.settings_goals_link_algorithm),
            leadingIconRes = R.drawable.round_calculate_24,
            accent = AccentTone.SECONDARY,
            onClick = onAlgorithmClick
        )
    }
}

@Preview(showBackground = true, name = "Goals — default")
@Composable
private fun GoalsContentPreview() {
    MemowaveTheme {
        GoalsContent(
            uiState = GoalsUiState(
                weeklyWordGoal = 50,
                goalDays = setOf(1, 2, 3, 4, 5),
                reminderHour = 19,
                reminderMinute = 30
            ),
            onBackClick = {},
            onAlgorithmClick = {},
            onToggleDay = {},
            onReminderTimeChange = { _, _ -> },
            onWeeklyGoalChange = {},
            onWeeklyGoalCommit = {},
            formatWords = { "$it words" }
        )
    }
}
