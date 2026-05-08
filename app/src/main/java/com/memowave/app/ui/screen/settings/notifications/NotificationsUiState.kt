package com.memowave.app.ui.screen.settings.notifications

data class NotificationsUiState(
    val masterEnabled: Boolean = true,
    val studyRemindersEnabled: Boolean = true,
    val streakRiskEnabled: Boolean = false,
    val milestonesEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietStartHour: Int = 22,
    val quietStartMinute: Int = 0,
    val quietEndHour: Int = 8,
    val quietEndMinute: Int = 0,
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,
    val permissionDenied: Boolean = false
)
