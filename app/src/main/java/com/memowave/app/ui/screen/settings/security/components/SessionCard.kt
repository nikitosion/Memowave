package com.memowave.app.ui.screen.settings.security.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memowave.app.R
import com.memowave.app.domain.model.security.UserSession
import com.memowave.app.ui.common.settings.AccentIconChip
import com.memowave.app.ui.common.settings.AccentTone

@Composable
fun SessionCard(
    session: UserSession,
    isCurrent: Boolean,
    onRevokeClick: () -> Unit,
    modifier: Modifier = Modifier,
    revokeEnabled: Boolean = true,
) {
    val containerColor = if (isCurrent) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AccentIconChip(
                    iconRes = R.drawable.round_lock_24,
                    accent = if (isCurrent) AccentTone.PRIMARY else AccentTone.NEUTRAL,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = session.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.W600,
                    )
                    if (isCurrent) CurrentBadge()
                }
            }
            if (!isCurrent) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    OutlinedButton(
                        onClick = onRevokeClick,
                        enabled = revokeEnabled,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.security_sessions_revoke),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentBadge() {
    Row(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.security_sessions_current_chip),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}
