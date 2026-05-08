package com.memowave.app.core.media

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUrlResolver @Inject constructor(
    private val baseUrl: String
) {
    fun resolve(fileName: String): String {
        val normalizedBase = baseUrl.trimEnd('/')
        return "$normalizedBase/images/$fileName"
    }
}
