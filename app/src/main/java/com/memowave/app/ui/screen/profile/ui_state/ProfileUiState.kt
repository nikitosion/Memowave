package com.memowave.app.ui.screen.profile.ui_state

import com.memowave.app.domain.model.User

data class ProfileUiState(
    val user: User = User(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)