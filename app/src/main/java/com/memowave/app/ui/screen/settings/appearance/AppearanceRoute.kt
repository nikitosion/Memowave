package com.memowave.app.ui.screen.settings.appearance

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.settings.ThemeMode
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.common.settings.SettingsToggleRow
import com.memowave.app.ui.common.settings.SettingsValueRow
import com.memowave.app.ui.screen.settings.appearance.components.LanguageBottomSheet
import com.memowave.app.ui.screen.settings.appearance.components.SegmentedThemePicker
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun AppearanceRoute(
    navController: NavController,
    viewModel: AppearanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.appLanguage) {
        val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        val target = uiState.appLanguage.orEmpty()
        if (current != target) {
            AppCompatDelegate.setApplicationLocales(
                if (target.isBlank()) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(target)
            )
        }
    }

    val languageLabel = when (uiState.appLanguage) {
        null -> stringResource(R.string.settings_appearance_language_system)
        "ru" -> stringResource(R.string.settings_appearance_language_ru)
        "en" -> stringResource(R.string.settings_appearance_language_en)
        else -> uiState.appLanguage.orEmpty()
    }

    AppearanceContent(
        uiState = uiState,
        languageLabel = languageLabel,
        onBackClick = { navController.popBackStack() },
        onSelectTheme = viewModel::selectTheme,
        onShowLanguageSheet = viewModel::showLanguageSheet,
        onDismissLanguageSheet = viewModel::dismissLanguageSheet,
        onSelectLanguage = viewModel::selectLanguage,
        onToggleReduceMotion = viewModel::toggleReduceMotion
    )
}

@Composable
private fun AppearanceContent(
    uiState: AppearanceUiState,
    languageLabel: String,
    onBackClick: () -> Unit,
    onSelectTheme: (ThemeMode) -> Unit,
    onShowLanguageSheet: () -> Unit,
    onDismissLanguageSheet: () -> Unit,
    onSelectLanguage: (String?) -> Unit,
    onToggleReduceMotion: (Boolean) -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_appearance),
        onBackClick = onBackClick
    ) {
        SettingsSection(title = stringResource(R.string.settings_appearance_section_theme)) {
            SegmentedThemePicker(
                selected = uiState.themeMode,
                onSelect = onSelectTheme
            )
        }

        SettingsSection(title = stringResource(R.string.settings_appearance_section_language)) {
            SettingsValueRow(
                title = stringResource(R.string.settings_appearance_language_label),
                value = languageLabel,
                onClick = onShowLanguageSheet
            )
        }

        SettingsSection(title = stringResource(R.string.settings_appearance_section_animations)) {
            SettingsToggleRow(
                label = stringResource(R.string.settings_appearance_reduce_motion),
                checked = uiState.reduceMotion,
                onCheckedChange = onToggleReduceMotion,
                supportingText = stringResource(R.string.settings_appearance_reduce_motion_supporting)
            )
        }
    }

    if (uiState.languageSheetShown) {
        LanguageBottomSheet(
            selected = uiState.appLanguage,
            onSelect = onSelectLanguage,
            onDismiss = onDismissLanguageSheet
        )
    }
}

@Preview(showBackground = true, name = "Appearance — default")
@Composable
private fun AppearanceContentPreview() {
    MemowaveTheme {
        AppearanceContent(
            uiState = AppearanceUiState(
                themeMode = ThemeMode.SYSTEM,
                appLanguage = null,
                reduceMotion = false
            ),
            languageLabel = "System",
            onBackClick = {},
            onSelectTheme = {},
            onShowLanguageSheet = {},
            onDismissLanguageSheet = {},
            onSelectLanguage = {},
            onToggleReduceMotion = {}
        )
    }
}

@Preview(showBackground = true, name = "Appearance — dark + reduce motion")
@Composable
private fun AppearanceContentDarkPreview() {
    MemowaveTheme(themeMode = ThemeMode.DARK) {
        AppearanceContent(
            uiState = AppearanceUiState(
                themeMode = ThemeMode.DARK,
                appLanguage = "ru",
                reduceMotion = true
            ),
            languageLabel = "Русский",
            onBackClick = {},
            onSelectTheme = {},
            onShowLanguageSheet = {},
            onDismissLanguageSheet = {},
            onSelectLanguage = {},
            onToggleReduceMotion = {}
        )
    }
}
