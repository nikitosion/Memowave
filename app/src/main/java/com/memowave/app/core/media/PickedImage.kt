package com.memowave.app.core.media

data class PickedImage(
    val bytes: ByteArray,
    val mimeType: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PickedImage) return false
        return mimeType == other.mimeType && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = 31 * mimeType.hashCode() + bytes.contentHashCode()
}
