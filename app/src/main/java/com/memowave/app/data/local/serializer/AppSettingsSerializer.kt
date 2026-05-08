package com.memowave.app.data.local.serializer

import androidx.datastore.core.Serializer
import com.memowave.app.domain.model.settings.AppSettings
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings()

    // ignoreUnknownKeys: tolerate fields removed from AppSettings between releases —
    // existing user files keep deserializing instead of falling back to defaults.
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            json.decodeFromString(
                AppSettings.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(
            json.encodeToString(AppSettings.serializer(), t).encodeToByteArray()
        )
    }
}
