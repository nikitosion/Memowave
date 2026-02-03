package com.memowave.app

import androidx.lifecycle.ViewModel
import com.memowave.app.core.auth.AuthStateManager
import com.memowave.app.ui.common.notification.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val authStateManager: AuthStateManager,
    val notificationManager: NotificationManager
) : ViewModel()