package com.memowave.app.data.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Json.Default.encodeToString(
            ListSerializer(String.Companion.serializer()),
            value ?: emptyList()
        )
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.Default.decodeFromString(ListSerializer(String.serializer()), value)
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? =
        value?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? =
        value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) }
}