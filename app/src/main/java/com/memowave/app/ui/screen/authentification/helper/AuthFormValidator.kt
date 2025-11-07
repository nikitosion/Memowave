package com.memowave.app.ui.screen.authentification.helper

import com.memowave.app.domain.validator.EmailValidator
import com.memowave.app.domain.validator.PasswordValidator
import com.memowave.app.domain.validator.UsernameValidator
import com.memowave.app.ui.screen.authentification.components.ui_state.ForgotPasswordFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.LoginFormState
import com.memowave.app.ui.screen.authentification.components.ui_state.SignUpFormState
import javax.inject.Inject

/**
 * Validates all authentication-related forms using domain validators.
 *
 * Delegates validation to email, password, and username validators.
 * Keeps UI state logic clean and reusable.
 *
 * - Validates login, sign up, forgot password, and reset password forms
 * - Returns updated form state with validation results and error messages
 *
 * @property emailValidator Validator for email fields
 * @property passwordValidator Validator for password fields
 * @property usernameValidator Validator for username fields
 */
class AuthFormValidator @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val usernameValidator: UsernameValidator
) {
    /**
     * Validates email in the login form.
     * @param currentState Current login form state
     * @param newEmail New email value
     * @return Updated login form state
     */
    fun validateLoginEmail(currentState: LoginFormState, newEmail: String): LoginFormState {
        val result = emailValidator.validate(newEmail)
        return currentState.copy(
            email = newEmail,
            isEmailValid = result.isValid,
            emailError = if (newEmail.isEmpty()) null else result.errorMessage
        )
    }

    /**
     * Validates password in the login form (length only).
     * @param currentState Current login form state
     * @param newPassword New password value
     * @return Updated login form state
     */
    fun validateLoginPassword(currentState: LoginFormState, newPassword: String): LoginFormState {
        val isValid = passwordValidator.validateLength(newPassword)
        return currentState.copy(
            password = newPassword,
            isPasswordValid = isValid,
            passwordError = null
        )
    }

    /**
     * Validates username in the sign up form.
     * @param currentState Current sign up form state
     * @param newName New username value
     * @return Updated sign up form state
     */
    fun validateSignUpName(currentState: SignUpFormState, newName: String): SignUpFormState {
        val result = usernameValidator.validate(newName)
        return currentState.copy(
            username = newName,
            isNameValid = result.isValid,
            nameError = if (newName.isEmpty()) null else result.errorMessage
        )
    }

    /**
     * Validates email in the sign up form.
     * @param currentState Current sign up form state
     * @param newEmail New email value
     * @return Updated sign up form state
     */
    fun validateSignUpEmail(currentState: SignUpFormState, newEmail: String): SignUpFormState {
        val result = emailValidator.validate(newEmail)
        return currentState.copy(
            email = newEmail,
            isEmailValid = result.isValid,
            emailError = if (newEmail.isEmpty()) null else result.errorMessage
        )
    }

    /**
     * Validates password in the sign up form (all rules).
     * @param currentState Current sign up form state
     * @param newPassword New password value
     * @return Updated sign up form state
     */
    fun validateSignUpPassword(currentState: SignUpFormState, newPassword: String): SignUpFormState {
        val passwordResult = passwordValidator.validate(newPassword)
        return currentState.copy(
            password = newPassword,
            isPasswordValid = passwordResult.isValid,
            passwordValidationState = passwordResult.validationState
        )
    }

    /**
     * Validates repeated password in the sign up form.
     * @param currentState Current sign up form state
     * @param newRepeatedPassword New repeated password value
     * @return Updated sign up form state
     */
    fun validateSignUpRepeatedPassword(
        currentState: SignUpFormState,
        newRepeatedPassword: String
    ): SignUpFormState {
        val result = passwordValidator.validateMatch(currentState.password, newRepeatedPassword)
        return currentState.copy(
            repeatedPassword = newRepeatedPassword,
            isRepeatedPasswordValid = result.isValid,
            repeatedPasswordError = if (newRepeatedPassword.isEmpty()) null else result.errorMessage
        )
    }

    /**
     * Validates email in the forgot password form.
     * @param currentState Current forgot password form state
     * @param newEmail New email value
     * @return Updated forgot password form state
     */
    fun validateForgotPasswordEmail(
        currentState: ForgotPasswordFormState,
        newEmail: String
    ): ForgotPasswordFormState {
        val result = emailValidator.validate(newEmail)
        return currentState.copy(
            email = newEmail,
            isEmailValid = result.isValid,
            emailError = if (newEmail.isEmpty()) null else result.errorMessage
        )
    }

    /**
     * Validates new password in the reset password form.
     * @param currentState Current forgot password form state
     * @param newPassword New password value
     * @return Updated forgot password form state
     */
    fun validateResetNewPassword(
        currentState: ForgotPasswordFormState,
        newPassword: String
    ): ForgotPasswordFormState {
        val passwordResult = passwordValidator.validate(newPassword)
        return currentState.copy(
            newPassword = newPassword,
            isNewPasswordValid = passwordResult.isValid,
            passwordValidationState = passwordResult.validationState
        )
    }

    /**
     * Validates repeated new password in the reset password form.
     * @param currentState Current forgot password form state
     * @param newRepeatedPassword New repeated password value
     * @return Updated forgot password form state
     */
    fun validateResetRepeatedPassword(
        currentState: ForgotPasswordFormState,
        newRepeatedPassword: String
    ): ForgotPasswordFormState {
        val result = passwordValidator.validateMatch(currentState.newPassword, newRepeatedPassword)
        return currentState.copy(
            repeatedNewPassword = newRepeatedPassword,
            isRepeatedNewPasswordValid = result.isValid,
            repeatedNewPasswordError = if (newRepeatedPassword.isEmpty()) null else result.errorMessage
        )
    }
}
