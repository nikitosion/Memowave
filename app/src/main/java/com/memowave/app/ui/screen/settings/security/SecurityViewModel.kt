package com.memowave.app.ui.screen.settings.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memowave.app.R
import com.memowave.app.core.util.StringProvider
import com.memowave.app.data.local.TokenManager
import com.memowave.app.domain.model.security.UserSession
import com.memowave.app.domain.usecase.security.DenySessionUseCase
import com.memowave.app.domain.usecase.security.GetActiveSessionsUseCase
import com.memowave.app.ui.common.notification.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val getActiveSessionsUseCase: GetActiveSessionsUseCase,
    private val denySessionUseCase: DenySessionUseCase,
    private val tokenManager: TokenManager,
    private val notificationManager: NotificationManager,
    private val strings: StringProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.sessionId.collect { id ->
                _uiState.update { it.copy(currentSessionId = id) }
            }
        }
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getActiveSessionsUseCase().fold(
                onSuccess = { sessions ->
                    _uiState.update { it.copy(sessions = sessions, isLoading = false) }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = strings.getString(R.string.security_sessions_load_error),
                        )
                    }
                }
            )
        }
    }

    fun onRevokeClick(session: UserSession) {
        if (session.sessionId == _uiState.value.currentSessionId) return
        _uiState.update { it.copy(pendingRevoke = session) }
    }

    fun dismissRevokeDialog() {
        _uiState.update { it.copy(pendingRevoke = null) }
    }

    fun confirmRevoke() {
        val session = _uiState.value.pendingRevoke ?: return
        _uiState.update { it.copy(pendingRevoke = null, isRevoking = true) }
        viewModelScope.launch {
            denySessionUseCase(session.sessionId).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            sessions = state.sessions.filterNot { it.sessionId == session.sessionId },
                            isRevoking = false,
                        )
                    }
                    notificationManager.showSuccess(
                        strings.getString(R.string.security_sessions_revoke_success)
                    )
                    load()
                },
                onFailure = {
                    _uiState.update { it.copy(isRevoking = false) }
                    notificationManager.showError(
                        strings.getString(R.string.security_sessions_revoke_error)
                    )
                }
            )
        }
    }
}
