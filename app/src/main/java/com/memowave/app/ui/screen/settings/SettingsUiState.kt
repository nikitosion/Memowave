package com.memowave.app.ui.screen.settings

import com.memowave.app.domain.model.user.User

data class SettingsUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val logoutDialogShown: Boolean = false
)
