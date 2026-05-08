package com.memowave.app.ui.screen.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.memowave.app.domain.model.user.User
import com.memowave.app.ui.common.media.UserAvatar
import com.memowave.app.ui.common.settings.ChevronTrailing

/**
 * Карточка пользователя для шапки экранов профиля и настроек.
 *
 * Принимает доменный [User] напрямую — не дублирует поля и сразу подтягивает
 * `imageUrl` через [UserAvatar]. Тап → переход на /settings/personal.
 */
@Composable
fun ProfileHeaderCard(
    user: User?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fallbackUsername: String = "—",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        UserAvatar(imageUrl = user?.imageUrl, size = 56.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = user?.username?.takeIf { !it.isNullOrBlank() } ?: fallbackUsername,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = user?.email.orEmpty(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        ChevronTrailing()
    }
}
