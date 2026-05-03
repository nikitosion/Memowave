package com.memowave.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.memowave.app.core.security.CryptoManager
import com.memowave.app.di.ApplicationScope
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager,
    @ApplicationScope private val appScope: CoroutineScope,
) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    /** Reactive stream of the decrypted JWT token (or null if missing). */
    val token: Flow<String?> = dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]?.let { encrypted -> cryptoManager.decryptString(encrypted) }
    }

    private val _cachedToken = MutableStateFlow<String?>(null)

    /**
     * In-memory snapshot of the latest token value. Read synchronously by
     * non-suspend call sites such as [com.memowave.app.data.remote.interceptor.AuthInterceptor].
     *
     * Populated synchronously at app startup via [primeBlocking] and kept in
     * sync with disk by a collector launched in [init].
     */
    val cachedToken: StateFlow<String?> = _cachedToken.asStateFlow()

    init {
        // Keep the in-memory cache in sync with disk for the lifetime of the app.
        appScope.launch {
            token.collect { _cachedToken.value = it }
        }
    }

    /**
     * Synchronously primes [cachedToken] from disk. Call exactly once from
     * [android.app.Application.onCreate] *before* any network request can fire,
     * so [com.memowave.app.data.remote.interceptor.AuthInterceptor] sees a non-null
     * token on the very first request after a cold start.
     *
     * Cost: one DataStore read (typically a few milliseconds) on the Main thread
     * at app startup. Bounded and one-time.
     */
    fun primeBlocking() {
        _cachedToken.value = runBlocking { token.first() }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = cryptoManager.encryptString(token)
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
