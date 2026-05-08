package com.memowave.app.core.security

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.StreamingAead
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingKeyManager
import com.google.crypto.tink.streamingaead.StreamingAeadConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val streamingAead: StreamingAead by lazy {
        StreamingAeadConfig.register()

        val keyTemplate = AesGcmHkdfStreamingKeyManager.aes256GcmHkdf4KBTemplate()

        AndroidKeysetManager.Builder()
            .withSharedPref(context, "memowave_master_key", "memowave_master_key_prefs")
            .withKeyTemplate(keyTemplate)
            .withMasterKeyUri("android-keystore://memowave_master_key")
            .build()
            .keysetHandle
            .getPrimitive(StreamingAead::class.java)
    }

    fun encrypt(outputStream: OutputStream): OutputStream {
        return streamingAead.newEncryptingStream(outputStream, byteArrayOf())
    }

    fun decrypt(inputStream: InputStream): InputStream {
        return streamingAead.newDecryptingStream(inputStream, byteArrayOf())
    }

    fun encryptString(plaintext: String): String {
        val outputStream = ByteArrayOutputStream()
        val encryptingStream = encrypt(outputStream)

        encryptingStream.use {
            it.write(plaintext.toByteArray())
        }

        val encryptedBytes = outputStream.toByteArray()
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    fun decryptString(encryptedBase64: String): String? {
        return try {
            val encryptedBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val inputStream = ByteArrayInputStream(encryptedBytes)
            val decryptingStream = decrypt(inputStream)

            decryptingStream.use {
                it.readBytes().decodeToString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}