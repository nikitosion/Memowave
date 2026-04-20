package com.memowave.app.ui.common.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.LocalAppWarningColors
import com.memowave.app.ui.theme.MemowaveTheme

/**
 * Pill-shaped notification toast with semantic colors per [NotificationType].
 *
 * Stateless — visibility and dismissal are controlled by the caller.
 * Typical usage is via [NotificationHost], which wraps this in `AnimatedVisibility`.
 *
 * @param message Text to display.
 * @param type Semantic type; drives container, content and icon colors.
 * @param onDismiss If non-null, the whole surface becomes clickable and invokes this.
 */
@Composable
fun MemowaveNotification(
    message: String,
    type: NotificationType,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    val style = notificationStyle(type)

    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .let { if (onDismiss != null) it.clickable { onDismiss() } else it },
        shape = RoundedCornerShape(20.dp),
        color = style.container,
        contentColor = style.content,
        tonalElevation = 3.dp,
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(style.iconRes),
                contentDescription = null,
                tint = style.accent,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall,
                color = style.content,
            )
        }
    }
}

private data class NotificationStyle(
    val container: Color,
    val content: Color,
    val accent: Color,
    val iconRes: Int,
)

@Composable
private fun notificationStyle(type: NotificationType): NotificationStyle = when (type) {
    NotificationType.Success -> NotificationStyle(
        container = MaterialTheme.colorScheme.primaryContainer,
        content = MaterialTheme.colorScheme.onPrimaryContainer,
        accent = MaterialTheme.colorScheme.primary,
        iconRes = R.drawable.round_check_24,
    )
    NotificationType.Error -> NotificationStyle(
        container = MaterialTheme.colorScheme.errorContainer,
        content = MaterialTheme.colorScheme.onErrorContainer,
        accent = MaterialTheme.colorScheme.error,
        iconRes = R.drawable.round_error_24,
    )
    NotificationType.Info -> NotificationStyle(
        container = MaterialTheme.colorScheme.secondaryContainer,
        content = MaterialTheme.colorScheme.onSecondaryContainer,
        accent = MaterialTheme.colorScheme.secondary,
        iconRes = R.drawable.round_info_24,
    )
    NotificationType.Warning -> {
        val warning = LocalAppWarningColors.current
        NotificationStyle(
            container = warning.container,
            content = warning.onContainer,
            accent = warning.accent,
            iconRes = R.drawable.round_warning_24,
        )
    }
}

@Preview(showBackground = true, name = "Success")
@Composable
private fun MemowaveNotificationSuccessPreview() {
    MemowaveTheme {
        MemowaveNotification(
            message = "С возвращением!",
            type = NotificationType.Success,
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun MemowaveNotificationErrorPreview() {
    MemowaveTheme {
        MemowaveNotification(
            message = "Сессия истекла. Войдите снова.",
            type = NotificationType.Error,
        )
    }
}

@Preview(showBackground = true, name = "Info")
@Composable
private fun MemowaveNotificationInfoPreview() {
    MemowaveTheme {
        MemowaveNotification(
            message = "Синхронизация завершена",
            type = NotificationType.Info,
        )
    }
}

@Preview(showBackground = true, name = "Warning")
@Composable
private fun MemowaveNotificationWarningPreview() {
    MemowaveTheme {
        MemowaveNotification(
            message = "Нет соединения с интернетом",
            type = NotificationType.Warning,
        )
    }
}
