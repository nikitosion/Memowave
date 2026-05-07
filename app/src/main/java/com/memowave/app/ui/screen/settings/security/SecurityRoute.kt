package com.memowave.app.ui.screen.settings.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.security.UserSession
import com.memowave.app.ui.common.settings.ActionStyle
import com.memowave.app.ui.common.settings.SettingsActionRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.screen.settings.security.components.SessionCard
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun SecurityRoute(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SecurityContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onRevokeClick = viewModel::onRevokeClick,
        onConfirmRevoke = viewModel::confirmRevoke,
        onDismissRevoke = viewModel::dismissRevokeDialog,
        onRetry = viewModel::load,
    )
}

@Composable
private fun SecurityContent(
    uiState: SecurityUiState,
    onBackClick: () -> Unit,
    onRevokeClick: (UserSession) -> Unit,
    onConfirmRevoke: () -> Unit,
    onDismissRevoke: () -> Unit,
    onRetry: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_security),
        onBackClick = onBackClick,
    ) {
        when {
            uiState.isLoading && uiState.sessions.isEmpty() -> LoadingBlock()
            uiState.error != null && uiState.sessions.isEmpty() -> ErrorBlock(
                message = uiState.error,
                onRetry = onRetry,
            )
            uiState.sessions.isEmpty() -> EmptyBlock()
            else -> SessionsList(
                sessions = uiState.sessions,
                currentSessionId = uiState.currentSessionId,
                isRevoking = uiState.isRevoking,
                onRevokeClick = onRevokeClick,
            )
        }
    }

    if (uiState.pendingRevoke != null) {
        RevokeConfirmDialog(
            session = uiState.pendingRevoke,
            onConfirm = onConfirmRevoke,
            onDismiss = onDismissRevoke,
        )
    }
}

@Composable
private fun SessionsList(
    sessions: List<UserSession>,
    currentSessionId: Int?,
    isRevoking: Boolean,
    onRevokeClick: (UserSession) -> Unit,
) {
    val sorted = sessions.sortedByDescending { it.sessionId == currentSessionId }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = stringResource(R.string.security_sessions_header),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
        )
        sorted.forEach { session ->
            SessionCard(
                session = session,
                isCurrent = session.sessionId == currentSessionId,
                revokeEnabled = !isRevoking,
                onRevokeClick = { onRevokeClick(session) },
            )
        }
    }
}

@Composable
private fun LoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBlock() {
    SettingsSection {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = stringResource(R.string.security_sessions_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun ErrorBlock(message: String, onRetry: () -> Unit) {
    SettingsActionRow(
        title = message,
        actionLabel = stringResource(R.string.security_sessions_retry),
        onAction = onRetry,
        style = ActionStyle.OUTLINED,
    )
}

@Composable
private fun RevokeConfirmDialog(
    session: UserSession,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.security_sessions_revoke_confirm_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.security_sessions_revoke_confirm_message,
                    session.name,
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.security_sessions_revoke_confirm_yes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.security_sessions_revoke_confirm_cancel))
            }
        },
    )
}

@Preview(showBackground = true, name = "Security — sessions list")
@Composable
private fun SecurityContentPreview() {
    MemowaveTheme {
        SecurityContent(
            uiState = SecurityUiState(
                sessions = listOf(
                    UserSession(sessionId = 1, name = "Samsung SM-G998B (Android 13)"),
                    UserSession(sessionId = 2, name = "Pixel 7 (Android 14)"),
                ),
                currentSessionId = 1,
            ),
            onBackClick = {},
            onRevokeClick = {},
            onConfirmRevoke = {},
            onDismissRevoke = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Security — empty")
@Composable
private fun SecurityContentEmptyPreview() {
    MemowaveTheme {
        SecurityContent(
            uiState = SecurityUiState(),
            onBackClick = {},
            onRevokeClick = {},
            onConfirmRevoke = {},
            onDismissRevoke = {},
            onRetry = {},
        )
    }
}
