package com.memowave.app.ui.screen.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.memowave.app.R
import com.memowave.app.ui.screen.settings.AppSetting
import com.memowave.app.ui.theme.MemowaveTheme

enum class SettingsBlockPreset {
    SIMPLE, EXIT
}

@Composable
fun SettingsBlock(
    appSettingsList: List<AppSetting>,
    preset: SettingsBlockPreset = SettingsBlockPreset.SIMPLE
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when (preset) {
                        SettingsBlockPreset.SIMPLE -> MaterialTheme.colorScheme.surfaceContainer
                        SettingsBlockPreset.EXIT -> MaterialTheme.colorScheme.errorContainer
                    }, shape = RoundedCornerShape(40.dp)
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            for (setting in appSettingsList) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            setting.onClick()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(25.dp),
                        painter = painterResource(setting.iconResId),
                        contentDescription = "User badge icon",
                        tint = when (preset) {
                            SettingsBlockPreset.SIMPLE -> LocalContentColor.current
                            SettingsBlockPreset.EXIT -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .padding(vertical = 12.dp),
                        text = setting.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = when (preset) {
                            SettingsBlockPreset.SIMPLE -> Color.Unspecified
                            SettingsBlockPreset.EXIT -> MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
                if (setting != appSettingsList.last()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsBlockPreview() {
    MemowaveTheme {
        SettingsBlock(
            appSettingsList = listOf(
                AppSetting("Appearance", R.drawable.round_badge_24, ""),
                AppSetting("Notifications", R.drawable.round_lock_24, ""),
                AppSetting("Settings", R.drawable.round_settings_24, ""),
                AppSetting("About", R.drawable.round_info_24, ""),
                AppSetting("Report a bug", R.drawable.round_bug_report_24, "")
            )
        )
    }
}