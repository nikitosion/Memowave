package com.memowave.app.ui.common.notification

sealed class NotificationType {
    data object Success : NotificationType()
    data object Error : NotificationType()
    data object Info : NotificationType()
    data object Warning : NotificationType()
}
