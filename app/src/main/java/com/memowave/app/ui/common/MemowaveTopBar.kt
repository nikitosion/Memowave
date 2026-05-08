package com.memowave.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.theme.MemowaveTheme

private val ICON_BUTTON_SIZE = 48.dp

/**
 * Reusable top bar with consistent back button placement and centered title.
 *
 * Right slot reserves at least the same width as the back button so the title
 * stays visually centered even when no actions are provided.
 *
 * @param onBackClick Callback for back navigation. If null, back button is hidden.
 * @param title Optional center title text.
 * @param actions Optional trailing content (settings button, XP badge, etc.).
 */
@Composable
fun MemowaveTopBar(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    bottomContent: @Composable ColumnScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading: back button or spacer for alignment
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.round_chevron_left_24),
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Center: title
            if (title != null) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = if (onBackClick != null) Alignment.Center else Alignment.CenterStart
                ) {
                    MixedFontText(
                        text = title,
                        accentFontFamily = FontFamily(
                            Font(R.font.bagelfatone_regular)
                        ),
                        fontWeight = FontWeight.W500
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f))
            }

            // Trailing: actions, balanced with back button width when there is a back button
            Row(
                modifier = Modifier.widthIn(min = if (onBackClick != null) ICON_BUTTON_SIZE else 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions
            )
        }

        bottomContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun MemowaveTopBarBackOnlyPreview() {
    MemowaveTheme {
        MemowaveTopBar(onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun MemowaveTopBarWithTitlePreview() {
    MemowaveTheme {
        MemowaveTopBar(
            onBackClick = {},
            title = "3/15 • Изучение"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MemowaveTopBarWithActionsPreview() {
    MemowaveTheme {
        MemowaveTopBar(
            onBackClick = {},
            title = "Настройки"
        ) {
            Icon(
                painter = painterResource(R.drawable.round_settings_24),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MemowaveTopBarWithActionsPreviewAndProgressBar() {
    MemowaveTheme {
        MemowaveTopBar(
            onBackClick = {},
            title = "Настройки",
            actions = {
                Icon(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = painterResource(R.drawable.round_settings_24),
                    contentDescription = null
                )
            },
            bottomContent = {
                LinearProgressIndicator(
                    progress = { 0.3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(10.dp),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
        )
    }
}
