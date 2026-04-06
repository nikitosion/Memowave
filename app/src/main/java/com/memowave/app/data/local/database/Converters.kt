package com.memowave.app.data.local.database

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

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
}