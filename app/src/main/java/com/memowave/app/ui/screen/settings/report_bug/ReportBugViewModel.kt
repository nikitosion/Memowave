package com.memowave.app.ui.screen.settings.report_bug

import androidx.lifecycle.ViewModel
import com.memowave.app.core.diagnostics.DeviceInfoCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReportBugViewModel @Inject constructor(
    private val deviceInfoCollector: DeviceInfoCollector
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportBugUiState())
    val uiState: StateFlow<ReportBugUiState> = _uiState.asStateFlow()

    init {
        val info = deviceInfoCollector.collect()
        _uiState.update {
            it.copy(
                diagnosticsLines = listOf(
                    "App version" to "${info.appVersion} (${info.appVersionCode})",
                    "Android" to "${info.androidVersion} (API ${info.apiLevel})",
                    "Device" to "${info.manufacturer} ${info.model}",
                    "Language" to info.locale,
                )
            )
        }
    }

    fun onCategoryChange(category: BugCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onIncludeDiagnosticsToggle(enabled: Boolean) {
        _uiState.update { it.copy(includeDiagnostics = enabled) }
    }

    /**
     * Возвращает (subject, body) для mailto-Intent.
     */
    fun composeReport(subjectFormat: String): Pair<String, String> {
        val state = _uiState.value
        val subject = subjectFormat.format(state.category.tag)
        val body = buildString {
            if (state.description.isNotBlank()) {
                appendLine(state.description.trim())
                appendLine()
            }
            if (state.includeDiagnostics) {
                appendLine("--- Diagnostics ---")
                state.diagnosticsLines.forEach { (k, v) -> appendLine("$k: $v") }
            }
        }
        return subject to body
    }
}
