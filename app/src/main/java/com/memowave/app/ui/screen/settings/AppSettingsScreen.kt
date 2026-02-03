package com.memowave.app.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.screen.profile.ProfileViewModel
import com.memowave.app.ui.screen.settings.components.SettingsBlock
import com.memowave.app.ui.screen.settings.components.SettingsBlockPreset
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun AppSettingsRoute(navController: NavController, profileViewModel: ProfileViewModel) {
    AppSettingsScreen(navController, profileViewModel)
}

@Composable
fun AppSettingsScreen(navController: NavController, profileViewModel: ProfileViewModel) {
    AppSettingsContent(
        onExitClick = {
            profileViewModel.logout()
        },
        onBackClick = { navController.popBackStack() }
    )
}

data class AppSetting(
    val name: String,
    val iconResId: Int,
    val settingScreenName: String,
    val onClick: () -> Unit = {}
)

@Composable
fun AppSettingsContent(
    onExitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val mainSettingsBlock = listOf(
        AppSetting("Personal information", R.drawable.round_badge_24, ""),
        AppSetting("Security", R.drawable.round_lock_24, ""),
        AppSetting("Goals and algorithm", R.drawable.round_calculate_24, "")
    )

    val secondSettingsBlock = listOf(
        AppSetting("Appearance", R.drawable.round_badge_24, ""),
        AppSetting("Notifications", R.drawable.round_lock_24, ""),
        AppSetting("Settings", R.drawable.round_settings_24, ""),
        AppSetting("About", R.drawable.round_info_24, ""),
        AppSetting("Report a bug", R.drawable.round_bug_report_24, "")
    )

    val exitSettingBlock = listOf(AppSetting("Exit", R.drawable.round_logout_24, "", onExitClick))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp, bottom = 32.dp)
    ) {
        IconButton(
            onClick = onBackClick
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.round_chevron_left_24),
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        SettingsBlock(mainSettingsBlock)
        SettingsBlock(secondSettingsBlock)
        SettingsBlock(exitSettingBlock, SettingsBlockPreset.EXIT)
    }
}

@Preview
@Composable
fun AppSettingsPreview() {
    MemowaveTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp
                    ),
            ) {
                AppSettingsContent({}, {})
            }
        }
    }
}