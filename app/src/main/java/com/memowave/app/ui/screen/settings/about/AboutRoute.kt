package com.memowave.app.ui.screen.settings.about

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.memowave.app.BuildConfig
import com.memowave.app.R
import com.memowave.app.ui.common.notification.NotificationManager
import com.memowave.app.ui.common.settings.AccentTone
import com.memowave.app.ui.common.settings.SettingsRow
import com.memowave.app.ui.common.settings.SettingsScaffold
import com.memowave.app.ui.common.settings.SettingsSection
import com.memowave.app.ui.screen.settings.about.components.AppInfoCard
import com.memowave.app.ui.theme.MemowaveTheme
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AboutEntryPoint {
    fun notificationManager(): NotificationManager
}

@Composable
fun AboutRoute(navController: NavController) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val notificationManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AboutEntryPoint::class.java
        ).notificationManager()
    }

    val versionLabel = stringResource(
        R.string.settings_about_version_format,
        BuildConfig.VERSION_NAME,
        BuildConfig.VERSION_CODE
    )
    val copiedMessage = stringResource(R.string.settings_about_version_copied)
    val comingSoon = stringResource(R.string.settings_about_coming_soon)
    val supportEmail = stringResource(R.string.settings_about_support_email)

    AboutContent(
        versionName = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
        supportEmail = supportEmail,
        onBackClick = { navController.popBackStack() },
        onCopyVersion = {
            clipboardManager.setText(AnnotatedString(versionLabel))
            notificationManager.showSuccess(copiedMessage)
        },
        onComingSoon = { notificationManager.showInfo(comingSoon) },
        onSupportClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$supportEmail")
            }
            runCatching { context.startActivity(intent) }
                .onFailure { notificationManager.showError(comingSoon) }
        }
    )
}

@Composable
private fun AboutContent(
    versionName: String,
    versionCode: Int,
    supportEmail: String,
    onBackClick: () -> Unit,
    onCopyVersion: () -> Unit,
    onComingSoon: () -> Unit,
    onSupportClick: () -> Unit,
) {
    SettingsScaffold(
        title = stringResource(R.string.settings_about),
        onBackClick = onBackClick
    ) {
        AppInfoCard(
            versionName = versionName,
            versionCode = versionCode,
            onClick = onCopyVersion
        )

        SettingsSection(title = stringResource(R.string.settings_about_section_documents)) {
            SettingsRow(
                title = stringResource(R.string.settings_about_terms),
                onClick = onComingSoon
            )
            SettingsRow(
                title = stringResource(R.string.settings_about_privacy),
                onClick = onComingSoon
            )
            SettingsRow(
                title = stringResource(R.string.settings_about_licenses),
                onClick = onComingSoon
            )
        }

        SettingsSection(title = stringResource(R.string.settings_about_section_contacts)) {
            SettingsRow(
                title = stringResource(R.string.settings_about_support),
                supportingText = supportEmail,
                leadingIconRes = R.drawable.round_alternate_email_24,
                accent = AccentTone.SECONDARY,
                onClick = onSupportClick
            )
            SettingsRow(
                title = stringResource(R.string.settings_about_telegram),
                onClick = onComingSoon
            )
            SettingsRow(
                title = stringResource(R.string.settings_about_developer),
                onClick = onComingSoon
            )
        }
    }
}

@Preview(showBackground = true, name = "About")
@Composable
private fun AboutContentPreview() {
    MemowaveTheme {
        AboutContent(
            versionName = "0.1.0",
            versionCode = 1,
            supportEmail = "support@memowave.app",
            onBackClick = {},
            onCopyVersion = {},
            onComingSoon = {},
            onSupportClick = {}
        )
    }
}
