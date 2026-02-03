package com.memowave.app.ui.common.notification

sealed class NotificationType {
    data object Success : NotificationType()
    data object Error : NotificationType()
}

