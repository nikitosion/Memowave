package com.memowave.app.ui.screen.settings.report_bug

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.settings.about.AboutEntryPoint
import com.memowave.app.ui.screen.settings.report_bug.components.DiagnosticsAttachmentCard
import com.memowave.app.ui.screen.settings.report_bug.components.ReportBugCategoryChips
import com.memowave.app.ui.theme.MemowaveTheme
import dagger.hilt.android.EntryPointAccessors

@Composable
fun ReportBugRoute(
    navController: NavController,
    viewModel: ReportBugViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val notificationManager: NotificationManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AboutEntryPoint::class.java
        ).notificationManager()
    }

    val supportEmail = stringResource(R.string.settings_about_support_email)
    val subjectFormat = stringResource(R.string.settings_report_subject_format)
    val noEmailApp = stringResource(R.string.settings_report_no_email_app)
    val attachLabel = stringResource(R.string.settings_report_attach)

    ReportBugContent(
        uiState = uiState,
        attachLabel = attachLabel,
        onBackClick = { navController.popBackStack() },
        onCategoryChange = viewModel::onCategoryChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onIncludeDiagnosticsToggle = viewModel::onIncludeDiagnosticsToggle,
        onSubmit = {
            val (subject, body) = viewModel.composeReport(subjectFormat)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$supportEmail")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            runCatching { context.startActivity(intent) }
                .onFailure { notificationManager.showError(noEmailApp) }
        }
    )
}

@Composable
private fun ReportBugContent(
    uiState: ReportBugUiState,
    attachLabel: String,
    onBackClick: () -> Unit,
    onCategoryChange: (BugCategory) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIncludeDiagnosticsToggle: (Boolean) -> Unit,
    onSubmit: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_report_bug),
        onBackClick = onBackClick
    ) {
        Text(
            text = stringResource(R.string.settings_report_headline),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W500
        )

        ReportBugCategoryChips(
            selected = uiState.category,
            onSelect = onCategoryChange
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.settings_report_description_label)) },
            placeholder = { Text(stringResource(R.string.settings_report_description_placeholder)) },
            shape = RoundedCornerShape(20.dp),
            minLines = 4,
            maxLines = 8
        )

        DiagnosticsAttachmentCard(
            items = uiState.diagnosticsLines,
            isIncluded = uiState.includeDiagnostics,
            toggleLabel = attachLabel,
            onToggle = onIncludeDiagnosticsToggle
        )

        AuthActionButton(
            onClick = onSubmit,
            isEnabled = true,
            isLoading = false,
            text = stringResource(R.string.settings_report_send)
        )
    }
}

@Preview(showBackground = true, name = "Report bug")
@Composable
private fun ReportBugContentPreview() {
    MemowaveTheme {
        ReportBugContent(
            uiState = ReportBugUiState(
                category = BugCategory.Design,
                description = "Хочу видеть streak в шапке Profile экрана",
                includeDiagnostics = true,
                diagnosticsLines = listOf(
                    "App version" to "0.1.0 (1)",
                    "Android" to "14 (API 34)",
                    "Device" to "Google Pixel 8",
                    "Language" to "ru-RU"
                )
            ),
            attachLabel = "Прикладывать диагностические данные",
            onBackClick = {},
            onCategoryChange = {},
            onDescriptionChange = {},
            onIncludeDiagnosticsToggle = {},
            onSubmit = {}
        )
    }
}
