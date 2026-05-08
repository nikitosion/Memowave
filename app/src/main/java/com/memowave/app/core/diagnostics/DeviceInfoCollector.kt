package com.memowave.app.core.diagnostics

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.memowave.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfoCollector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    data class DeviceInfo(
        val appVersion: String,
        val appVersionCode: Int,
        val manufacturer: String,
        val model: String,
        val androidVersion: String,
        val apiLevel: Int,
        val locale: String,
    )

    fun collect(): DeviceInfo {
        val locale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            .ifBlank { Locale.getDefault().toLanguageTag() }
        return DeviceInfo(
            appVersion = BuildConfig.VERSION_NAME,
            appVersionCode = BuildConfig.VERSION_CODE,
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            locale = locale,
        )
    }

    /**
     * Human-readable label sent as `session` on login/register so the user can
     * recognise this device in the active-sessions list.
     */
    fun sessionName(): String {
        val info = collect()
        return "${info.manufacturer} ${info.model} (Android ${info.androidVersion})"
    }

    fun format(info: DeviceInfo): String = buildString {
        appendLine("App version: ${info.appVersion} (${info.appVersionCode})")
        appendLine("Android: ${info.androidVersion} (API ${info.apiLevel})")
        appendLine("Device: ${info.manufacturer} ${info.model}")
        appendLine("Language: ${info.locale}")
    }

    @Suppress("unused") // reserved for future "report bug from any screen" feature
    fun packageInfoOrNull() = try {
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
}
