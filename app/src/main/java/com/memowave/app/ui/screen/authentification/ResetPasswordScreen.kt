package com.memowave.app.ui.screen.authentification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.data.local.dao.UserDao
import com.memowave.app.data.local.entity.UserEntity
import com.memowave.app.data.mapper.UserMapper
import com.memowave.app.data.remote.api.ApiService
import com.memowave.app.data.repository.AuthRepositoryImpl
import com.memowave.app.domain.usecase.auth.GetUserByEmailUseCase
import com.memowave.app.domain.usecase.auth.LoginUseCase
import com.memowave.app.domain.usecase.auth.LogoutUseCase
import com.memowave.app.domain.usecase.auth.ResetPasswordUseCase
import com.memowave.app.domain.usecase.auth.SignUpUseCase
import com.memowave.app.ui.navigation.Screen
import com.memowave.app.ui.screen.authentification.components.AuthActionButton
import com.memowave.app.ui.screen.authentification.components.AuthSecuredTextField
import com.memowave.app.ui.screen.authentification.components.MemowaveLogoColored
import com.memowave.app.ui.screen.authentification.components.PasswordRule
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun ResetPasswordRoute(authViewModel: AuthViewModel, navController: NavController) {
    ResetPasswordScreen(viewModel = authViewModel, navController = navController)
}

@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState = viewModel.uiState.collectAsState().value
    val isResetPasswordButtonEnabled = viewModel.isResetPasswordButtonEnabled.collectAsState().value

    LaunchedEffect(uiState.isResetPasswordSuccess) {
        if (uiState.isResetPasswordSuccess) {
            navController.navigate("login") {
                popUpTo(Screen.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(top = 64.dp, bottom = 64.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MemowaveLogoColored(size = 60.dp)

            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = "Восстановление пароля",
                style = MaterialTheme.typography.headlineLarge,
            )

            var isError: String? = null
            with (uiState.forgotPasswordForm) {
                if (!newPassword.isEmpty() && !passwordValidationState.isAllValid) {
                    isError = "Пароль не соответствует требованиям"
                }
            }

            AuthSecuredTextField(
                value = uiState.forgotPasswordForm.newPassword,
                onValueChange = viewModel::onResetPasswordNewPasswordChanged,
                modifier = Modifier.padding(top = 16.dp),
                error = isError,
                labelText = "Новый пароль",
                placeholderText = "Придумайте новый пароль",
                leadingIconResId = R.drawable.round_lock_24,
                imeAction = ImeAction.Next
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                rule = "Содержит не менее 8 символов",
                isSatisfied = uiState.forgotPasswordForm.passwordValidationState.hasMinLength
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит строчную букву",
                isSatisfied = uiState.forgotPasswordForm.passwordValidationState.hasLowercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит заглавную букву",
                isSatisfied = uiState.forgotPasswordForm.passwordValidationState.hasUppercase
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит цифру",
                isSatisfied = uiState.forgotPasswordForm.passwordValidationState.hasDigit
            )
            PasswordRule(
                modifier = Modifier.padding(start = 16.dp),
                rule = "Содержит специальный символ",
                isSatisfied = uiState.forgotPasswordForm.passwordValidationState.hasSpecialChar
            )

            AuthSecuredTextField(
                value = uiState.forgotPasswordForm.repeatedNewPassword,
                onValueChange = viewModel::onResetPasswordRepeatedNewPasswordChanged,
                error = uiState.forgotPasswordForm.repeatedNewPasswordError,
                modifier = Modifier.padding(top = 16.dp),
                labelText = "Подтвердите пароль",
                placeholderText = "Повторите пароль",
                leadingIconResId = R.drawable.round_lock_24,
            )

            AuthActionButton(
                onClick = viewModel::onResetPasswordClick,
                isEnabled = isResetPasswordButtonEnabled,
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(top = 20.dp),
                text = "Восстановить пароль"
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 64.dp),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.round_chevron_left_24),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun ResetPasswordScreenPreview() {
    MemowaveTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            val authRepImpl = AuthRepositoryImpl(
                ApiService(),
                object : UserDao {
                    override suspend fun createUser(user: UserEntity): Long {
                        TODO("Not yet implemented")
                    }

                    override suspend fun getCurrentUser(): UserEntity? {
                        TODO("Not yet implemented")
                    }

                    override suspend fun getUserByEmail(email: String): UserEntity? {
                        TODO("Not yet implemented")
                    }

                    override suspend fun getUserById(id: Long): UserEntity? {
                        TODO("Not yet implemented")
                    }

                    override suspend fun updatePassword(
                        id: Long,
                        password: String
                    ) {
                        TODO("Not yet implemented")
                    }

                    override suspend fun deleteUserById(id: Long) {
                        TODO("Not yet implemented")
                    }
                }, UserMapper()
            )

            ResetPasswordScreen(
                viewModel = AuthViewModel(
                    loginUseCase = LoginUseCase(authRepImpl),
                    resetPasswordUseCase = ResetPasswordUseCase(authRepImpl),
                    signUpUseCase = SignUpUseCase(authRepImpl),
                    logoutUseCase = LogoutUseCase(authRepImpl),
                    getUserByEmailUseCase = GetUserByEmailUseCase(authRepImpl)
                ), navController = NavController(LocalContext.current)
            )
        }
    }
}