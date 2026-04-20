package com.memowave.app.ui.common.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor() {
    private val _notificationMessage = MutableStateFlow<NotificationMessage?>(null)
    val notificationMessage: StateFlow<NotificationMessage?> = _notificationMessage.asStateFlow()

    fun showSuccess(message: String) {
        _notificationMessage.value = NotificationMessage(message, NotificationType.Success)
    }

    fun showError(message: String) {
        _notificationMessage.value = NotificationMessage(message, NotificationType.Error)
    }

    fun showInfo(message: String) {
        _notificationMessage.value = NotificationMessage(message, NotificationType.Info)
    }

    fun showWarning(message: String) {
        _notificationMessage.value = NotificationMessage(message, NotificationType.Warning)
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }
}

