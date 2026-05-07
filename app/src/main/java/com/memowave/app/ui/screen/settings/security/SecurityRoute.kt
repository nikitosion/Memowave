package com.memowave.app.ui.screen.settings.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.memowave.app.R
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.theme.MemowaveTheme

@Composable
fun SecurityRoute(navController: NavController) {
    SecurityContent(onBackClick = { navController.popBackStack() })
}

@Composable
private fun SecurityContent(onBackClick: () -> Unit) {
    SettingsScaffold(
        title = stringResource(R.string.settings_security),
        onBackClick = onBackClick
    ) {
        // Settings will live here in a future iteration. Empty by design for now.
    }
}

@Preview(showBackground = true, name = "Security — empty")
@Composable
private fun SecurityContentPreview() {
    MemowaveTheme {
        SecurityContent(onBackClick = {})
    }
}
