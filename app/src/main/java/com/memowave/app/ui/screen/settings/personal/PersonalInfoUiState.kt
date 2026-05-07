package com.memowave.app.ui.screen.settings.personal

import com.memowave.app.domain.model.user.User
import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState

data class PersonalInfoUiState(
    val user: User? = null,
    val isUserLoading: Boolean = false,
    val userError: String? = null,

    val editingUsername: String = "",
    val isSubmittingUsername: Boolean = false,
    val usernameError: String? = null,

    val passwordExpanded: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val repeatPassword: String = "",
    val passwordValidation: PasswordValidationState = PasswordValidationState(),
    val passwordsMatch: Boolean = false,
    val isSubmitting: Boolean = false,

    val deleteDialogShown: Boolean = false
) {
    val canSubmitPassword: Boolean
        get() = !isSubmitting &&
            currentPassword.isNotBlank() &&
            passwordValidation.isAllValid &&
            passwordsMatch

    val originalUsername: String
        get() = user?.username.orEmpty()
}
