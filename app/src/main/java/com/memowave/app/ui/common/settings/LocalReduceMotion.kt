package com.memowave.app.ui.common.settings

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Сигнал для UI: пользователь хочет уменьшить движение.
 * Анимации в зависимых композаблах должны укоротиться или отключиться.
 *
 * Значение приходит из `AppSettings.reduceMotion` через `Memowave()` в `MainActivity`.
 */
val LocalReduceMotion = staticCompositionLocalOf { false }
