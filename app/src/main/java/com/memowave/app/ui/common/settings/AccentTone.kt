package com.memowave.app.ui.common.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.memowave.app.ui.theme.LocalAppWarningColors

enum class AccentTone {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    WARNING,
    ERROR,
    NEUTRAL
}

data class AccentColors(
    val container: Color,
    val onContainer: Color
)

@Composable
@ReadOnlyComposable
fun AccentTone.colors(): AccentColors {
    val cs = MaterialTheme.colorScheme
    val warn = LocalAppWarningColors.current
    return when (this) {
        AccentTone.PRIMARY -> AccentColors(cs.primaryContainer, cs.onPrimaryContainer)
        AccentTone.SECONDARY -> AccentColors(cs.secondaryContainer, cs.onSecondaryContainer)
        AccentTone.TERTIARY -> AccentColors(cs.tertiaryContainer, cs.onTertiaryContainer)
        AccentTone.WARNING -> AccentColors(warn.container, warn.onContainer)
        AccentTone.ERROR -> AccentColors(cs.errorContainer, cs.onErrorContainer)
        AccentTone.NEUTRAL -> AccentColors(cs.surfaceContainerHigh, cs.onSurface)
    }
}
