package com.memowave.app.ui.screen.settings.algorithm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.ActionStyle
import com.memowave.app.ui.common.settings.LabeledSlider
import com.memowave.app.ui.common.settings.SettingsActionRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.screen.settings.algorithm.components.IntervalProjectionStrip
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun AlgorithmRoute(
    navController: NavController,
    viewModel: AlgorithmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AlgorithmContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onRetentionChange = viewModel::onRetentionChange,
        onRetentionCommit = viewModel::onRetentionCommit,
        onMaxIntervalChange = viewModel::onMaxIntervalChange,
        onMaxIntervalCommit = viewModel::onMaxIntervalCommit,
        onEasyBonusChange = viewModel::onEasyBonusChange,
        onEasyBonusCommit = viewModel::onEasyBonusCommit,
        onHardPenaltyChange = viewModel::onHardPenaltyChange,
        onHardPenaltyCommit = viewModel::onHardPenaltyCommit,
        onResetRequest = viewModel::showResetDialog,
        onResetDismiss = viewModel::dismissResetDialog,
        onResetConfirm = viewModel::confirmReset,
        formatMaxInterval = { value ->
            val days = value.toInt()
            if (days >= 365) context.getString(
                R.string.settings_algorithm_years_format,
                days / 365f
            ) else context.getString(R.string.settings_algorithm_days_format, days)
        }
    )
}

@Composable
private fun AlgorithmContent(
    uiState: AlgorithmUiState,
    onBackClick: () -> Unit,
    onRetentionChange: (Float) -> Unit,
    onRetentionCommit: () -> Unit,
    onMaxIntervalChange: (Float) -> Unit,
    onMaxIntervalCommit: () -> Unit,
    onEasyBonusChange: (Float) -> Unit,
    onEasyBonusCommit: () -> Unit,
    onHardPenaltyChange: (Float) -> Unit,
    onHardPenaltyCommit: () -> Unit,
    onResetRequest: () -> Unit,
    onResetDismiss: () -> Unit,
    onResetConfirm: () -> Unit,
    formatMaxInterval: (Float) -> String,
) {
    val retentionForgetCount = ((1f - uiState.requestRetention) * 100).toInt()

    SettingsScaffold(
        title = stringResource(R.string.settings_algorithm),
        onBackClick = onBackClick
    ) {
        SettingsSection(title = stringResource(R.string.settings_algorithm_section_basic)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LabeledSlider(
                    title = stringResource(R.string.settings_algorithm_retention),
                    value = uiState.requestRetention,
                    onValueChange = onRetentionChange,
                    onValueChangeFinished = onRetentionCommit,
                    valueRange = 0.85f..0.98f,
                    steps = 12,
                    valueFormatter = { "${(it * 100).toInt()}%" },
                    helperLeft = stringResource(R.string.settings_algorithm_retention_helper_left),
                    helperRight = stringResource(R.string.settings_algorithm_retention_helper_right)
                )
                ExplainerText(
                    text = stringResource(
                        R.string.settings_algorithm_retention_explainer,
                        retentionForgetCount
                    )
                )
                IntervalProjectionStrip(retention = uiState.requestRetention)
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LabeledSlider(
                    title = stringResource(R.string.settings_algorithm_max_interval),
                    value = uiState.maximumInterval.toFloat(),
                    onValueChange = onMaxIntervalChange,
                    onValueChangeFinished = onMaxIntervalCommit,
                    valueRange = 30f..365f,
                    valueFormatter = formatMaxInterval
                )
                ExplainerText(
                    text = stringResource(R.string.settings_algorithm_max_interval_explainer)
                )
            }
        }

        SettingsSection(title = stringResource(R.string.settings_algorithm_section_fine)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LabeledSlider(
                    title = stringResource(R.string.settings_algorithm_easy_bonus),
                    value = uiState.easyBonus,
                    onValueChange = onEasyBonusChange,
                    onValueChangeFinished = onEasyBonusCommit,
                    valueRange = 1.0f..2.0f,
                    valueFormatter = { "%.2fx".format(it) }
                )
                ExplainerText(
                    text = stringResource(R.string.settings_algorithm_easy_bonus_explainer)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LabeledSlider(
                    title = stringResource(R.string.settings_algorithm_hard_penalty),
                    value = uiState.hardPenalty,
                    onValueChange = onHardPenaltyChange,
                    onValueChangeFinished = onHardPenaltyCommit,
                    valueRange = 0.5f..1.0f,
                    valueFormatter = { "%.2fx".format(it) }
                )
                ExplainerText(
                    text = stringResource(R.string.settings_algorithm_hard_penalty_explainer)
                )
            }
        }

        SettingsActionRow(
            title = stringResource(R.string.settings_algorithm_reset),
            actionLabel = stringResource(R.string.settings_algorithm_reset),
            onAction = onResetRequest,
            leadingIconRes = R.drawable.round_warning_24,
            accent = AccentTone.TERTIARY,
            style = ActionStyle.OUTLINED
        )
    }

    if (uiState.resetDialogShown) {
        AlertDialog(
            onDismissRequest = onResetDismiss,
            title = { Text(stringResource(R.string.settings_algorithm_reset)) },
            text = { Text(stringResource(R.string.settings_algorithm_reset_confirm)) },
            confirmButton = {
                TextButton(onClick = onResetConfirm) {
                    Text(
                        stringResource(R.string.settings_algorithm_reset),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onResetDismiss) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }
}

@Composable
private fun ExplainerText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
    )
}

@Preview(showBackground = true, name = "Algorithm — default 95%")
@Composable
private fun AlgorithmContentDefaultPreview() {
    MemowaveTheme {
        AlgorithmContent(
            uiState = AlgorithmUiState(),
            onBackClick = {},
            onRetentionChange = {},
            onRetentionCommit = {},
            onMaxIntervalChange = {},
            onMaxIntervalCommit = {},
            onEasyBonusChange = {},
            onEasyBonusCommit = {},
            onHardPenaltyChange = {},
            onHardPenaltyCommit = {},
            onResetRequest = {},
            onResetDismiss = {},
            onResetConfirm = {},
            formatMaxInterval = { value ->
                val days = value.toInt()
                if (days >= 365) "%.1f years".format(days / 365f) else "$days days"
            }
        )
    }
}

@Preview(showBackground = true, name = "Algorithm — aggressive 85%")
@Composable
private fun AlgorithmContentAggressivePreview() {
    MemowaveTheme {
        AlgorithmContent(
            uiState = AlgorithmUiState(
                requestRetention = 0.85f,
                maximumInterval = 365,
                easyBonus = 1.95f,
                hardPenalty = 0.55f
            ),
            onBackClick = {},
            onRetentionChange = {},
            onRetentionCommit = {},
            onMaxIntervalChange = {},
            onMaxIntervalCommit = {},
            onEasyBonusChange = {},
            onEasyBonusCommit = {},
            onHardPenaltyChange = {},
            onHardPenaltyCommit = {},
            onResetRequest = {},
            onResetDismiss = {},
            onResetConfirm = {},
            formatMaxInterval = { value ->
                val days = value.toInt()
                if (days >= 365) "%.1f years".format(days / 365f) else "$days days"
            }
        )
    }
}
