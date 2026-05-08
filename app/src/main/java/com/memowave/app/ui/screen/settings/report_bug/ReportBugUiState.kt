package com.memowave.app.ui.screen.settings.report_bug

enum class BugCategory(val labelRes: Int, val tag: String) {
    Crash(com.memowave.app.R.string.settings_report_category_crash, "Crash"),
    Design(com.memowave.app.R.string.settings_report_category_design, "Design"),
    Translation(com.memowave.app.R.string.settings_report_category_translation, "Translation"),
    Performance(com.memowave.app.R.string.settings_report_category_performance, "Performance"),
    Other(com.memowave.app.R.string.settings_report_category_other, "Other"),
}

data class ReportBugUiState(
    val category: BugCategory = BugCategory.Other,
    val description: String = "",
    val includeDiagnostics: Boolean = true,
    val diagnosticsLines: List<Pair<String, String>> = emptyList(),
)
