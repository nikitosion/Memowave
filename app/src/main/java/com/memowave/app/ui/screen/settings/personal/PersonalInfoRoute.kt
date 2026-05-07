package com.memowave.app.ui.screen.settings.personal

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.domain.model.user.User
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.SettingsExpandableRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.screen.authentification.components.ui_state.PasswordValidationState
import com.memowave.app.ui.screen.settings.components.DangerZoneCard
import com.memowave.app.ui.screen.settings.personal.components.ChangePasswordForm
import com.memowave.app.ui.screen.settings.personal.components.EmailReadOnlyField
import com.memowave.app.ui.screen.settings.personal.components.UsernameEditField
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun PersonalInfoRoute(
    navController: NavController,
    viewModel: PersonalInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadUser() }

    val passwordSuccess = stringResource(R.string.settings_personal_password_updated)
    val passwordWrong = stringResource(R.string.settings_personal_password_wrong)
    val passwordForbidden = stringResource(R.string.settings_personal_password_forbidden)
    val passwordServer = stringResource(R.string.settings_personal_password_server_error)
    val passwordSame = stringResource(R.string.settings_personal_same_password)
    val passwordInvalid = stringResource(R.string.password_not_valid)
    val passwordUnknown = stringResource(R.string.auth_error_login_failed)
    val deleteComingSoon = stringResource(R.string.settings_personal_delete_coming_soon)

    val usernameSuccess = stringResource(R.string.settings_personal_username_updated)
    val usernameTooShort = stringResource(R.string.settings_personal_username_too_short)
    val usernameTooLong = stringResource(R.string.settings_personal_username_too_long)
    val usernameGenericError = stringResource(R.string.settings_personal_username_update_failed)

    PersonalInfoContent(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onUsernameChange = viewModel::onUsernameChange,
        onUsernameSave = {
            viewModel.submitUsername(
                successMessage = usernameSuccess,
                errorTooShort = usernameTooShort,
                errorTooLong = usernameTooLong,
                errorGeneric = usernameGenericError
            )
        },
        onUsernameCancel = viewModel::cancelUsernameEdit,
        onTogglePasswordExpanded = viewModel::togglePasswordExpanded,
        onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
        onSubmitPassword = {
            viewModel.submitPassword(
                successMessage = passwordSuccess,
                errorWrong = passwordWrong,
                errorForbidden = passwordForbidden,
                errorServer = passwordServer,
                errorSame = passwordSame,
                errorInvalid = passwordInvalid,
                errorUnknown = passwordUnknown
            )
        },
        onDeleteRequest = viewModel::showDeleteDialog,
        onDeleteDismiss = viewModel::dismissDeleteDialog,
        onDeleteConfirm = { viewModel.confirmDelete(deleteComingSoon) }
    )
}

@Composable
private fun PersonalInfoContent(
    uiState: PersonalInfoUiState,
    onBackClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onUsernameSave: () -> Unit,
    onUsernameCancel: () -> Unit,
    onTogglePasswordExpanded: () -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSubmitPassword: () -> Unit,
    onDeleteRequest: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_personal),
        onBackClick = onBackClick
    ) {
        SettingsSection(title = stringResource(R.string.settings_personal_section_contact)) {
            EmailReadOnlyField(email = uiState.user?.email.orEmpty())
            UsernameEditField(
                value = uiState.editingUsername,
                originalValue = uiState.originalUsername,
                onValueChange = onUsernameChange,
                onSave = onUsernameSave,
                onCancel = onUsernameCancel,
                isSubmitting = uiState.isSubmittingUsername,
                error = uiState.usernameError
            )
        }

        SettingsSection(title = stringResource(R.string.settings_personal_section_security)) {
            SettingsExpandableRow(
                title = stringResource(R.string.settings_personal_change_password),
                expanded = uiState.passwordExpanded,
                onExpandedChange = { onTogglePasswordExpanded() },
                leadingIconRes = R.drawable.round_lock_24,
                accent = AccentTone.TERTIARY
            ) {
                ChangePasswordForm(
                    currentPassword = uiState.currentPassword,
                    newPassword = uiState.newPassword,
                    repeatPassword = uiState.repeatPassword,
                    validation = uiState.passwordValidation,
                    passwordsMatch = uiState.passwordsMatch,
                    isSubmitting = uiState.isSubmitting,
                    canSubmit = uiState.canSubmitPassword,
                    onCurrentChange = onCurrentPasswordChange,
                    onNewChange = onNewPasswordChange,
                    onRepeatChange = onRepeatPasswordChange,
                    onSubmit = onSubmitPassword
                )
            }
        }

        DangerZoneCard(
            title = stringResource(R.string.settings_personal_danger_zone),
            description = stringResource(R.string.settings_personal_delete_description),
            actionLabel = stringResource(R.string.settings_personal_delete_account),
            onAction = onDeleteRequest
        )
    }

    if (uiState.deleteDialogShown) {
        AlertDialog(
            onDismissRequest = onDeleteDismiss,
            title = { Text(stringResource(R.string.settings_personal_delete_account)) },
            text = { Text(stringResource(R.string.settings_personal_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = onDeleteConfirm) {
                    Text(
                        stringResource(R.string.settings_personal_delete_account),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteDismiss) {
                    Text(stringResource(R.string.settings_cancel))
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Personal info — collapsed")
@Composable
private fun PersonalInfoContentPreview() {
    MemowaveTheme {
        PersonalInfoContent(
            uiState = PersonalInfoUiState(
                user = User(id = 1, username = "Nikita", email = "nikita@example.com"),
                editingUsername = "Nikita"
            ),
            onBackClick = {},
            onUsernameChange = {},
            onUsernameSave = {},
            onUsernameCancel = {},
            onTogglePasswordExpanded = {},
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onRepeatPasswordChange = {},
            onSubmitPassword = {},
            onDeleteRequest = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}

@Preview(showBackground = true, name = "Personal info — password expanded")
@Composable
private fun PersonalInfoContentExpandedPreview() {
    MemowaveTheme {
        PersonalInfoContent(
            uiState = PersonalInfoUiState(
                user = User(id = 1, username = "Nikita", email = "nikita@example.com"),
                editingUsername = "Nikita",
                passwordExpanded = true,
                newPassword = "NewStr0ng!",
                passwordValidation = PasswordValidationState(
                    hasMinLength = true,
                    hasLowercase = true,
                    hasUppercase = true,
                    hasDigit = true,
                    hasSpecialChar = true
                )
            ),
            onBackClick = {},
            onUsernameChange = {},
            onUsernameSave = {},
            onUsernameCancel = {},
            onTogglePasswordExpanded = {},
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onRepeatPasswordChange = {},
            onSubmitPassword = {},
            onDeleteRequest = {},
            onDeleteDismiss = {},
            onDeleteConfirm = {}
        )
    }
}
