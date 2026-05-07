package com.memowave.app.ui.screen.settings.security

import com.memowave.app.domain.model.security.UserSession

data class SecurityUiState(
    val sessions: List<UserSession> = emptyList(),
    val currentSessionId: Int? = null,
    val isLoading: Boolean = false,
    val isRevoking: Boolean = false,
    val error: String? = null,
    val pendingRevoke: UserSession? = null,
)
