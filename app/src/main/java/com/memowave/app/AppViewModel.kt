package com.memowave.app

import androidx.lifecycle.ViewModel
import com.memowave.app.core.auth.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val authStateManager: AuthStateManager
) : ViewModel()