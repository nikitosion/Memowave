package com.memowave.app.core.util

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin testable wrapper around [Context.getString] for non-composable code
 * (ViewModels, repositories) that needs access to localized resources without
 * depending on a Compose [androidx.compose.ui.platform.LocalContext] or a raw
 * [Context] reference.
 */
@Singleton
class StringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes id: Int): String = context.getString(id)

    fun getString(@StringRes id: Int, vararg formatArgs: Any): String =
        context.getString(id, *formatArgs)
}
