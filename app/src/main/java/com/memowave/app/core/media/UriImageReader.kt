package com.memowave.app.core.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriImageReader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun read(uri: Uri): Result<PickedImage> = withContext(Dispatchers.IO) {
        try {
            val resolver: ContentResolver = context.contentResolver
            val mimeType = resolver.getType(uri) ?: "application/octet-stream"
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext Result.failure(
                    IllegalStateException("Cannot open input stream for $uri")
                )
            Result.success(PickedImage(bytes = bytes, mimeType = mimeType))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
