package com.memowave.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val SESSION_ID_KEY = intPreferencesKey("session_id")

    /** Reactive stream of the decrypted access token (or null if missing). */
    val accessToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[ACCESS_TOKEN_KEY]?.let { encrypted -> cryptoManager.decryptString(encrypted) }
    }

    private val _cachedAccessToken = MutableStateFlow<String?>(null)

    /**
     * In-memory snapshot of the latest access token. Read synchronously by
     * non-suspend call sites such as [com.memowave.app.data.remote.interceptor.AuthInterceptor].
     *
     * Populated synchronously at app startup via [primeBlocking] and kept in
     * sync with disk by a collector launched in [init].
     */
    val cachedAccessToken: StateFlow<String?> = _cachedAccessToken.asStateFlow()

    init {
        appScope.launch {
            accessToken.collect { _cachedAccessToken.value = it }
        }
    }

    /**
     * Synchronously primes [cachedAccessToken] from disk. Call exactly once from
     * [android.app.Application.onCreate] *before* any network request can fire,
     * so [com.memowave.app.data.remote.interceptor.AuthInterceptor] sees a non-null
     * token on the very first request after a cold start.
     *
     * Cost: one DataStore read (typically a few milliseconds) on the Main thread
     * at app startup. Bounded and one-time.
     */
    fun primeBlocking() {
        _cachedAccessToken.value = runBlocking { accessToken.first() }
    }

    /**
     * Reads the refresh token directly from disk. Refresh is needed only inside
     * [com.memowave.app.data.remote.interceptor.TokenAuthenticator] on a 401, so
     * we don't keep a long-lived in-memory cache of it.
     */
    suspend fun getRefreshToken(): String? {
        val prefs = dataStore.data.first()
        return prefs[REFRESH_TOKEN_KEY]?.let { cryptoManager.decryptString(it) }
    }

    /** Atomically writes both tokens; partial state is impossible. */
    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = cryptoManager.encryptString(access)
            prefs[REFRESH_TOKEN_KEY] = cryptoManager.encryptString(refresh)
        }
    }

    /** Reactive stream of the current session id (null if missing). */
    val sessionId: Flow<Int?> = dataStore.data.map { prefs -> prefs[SESSION_ID_KEY] }

    /** One-shot read used by the logout flow that fires `set-denied`. */
    suspend fun getSessionId(): Int? = dataStore.data.first()[SESSION_ID_KEY]

    suspend fun saveSessionId(id: Int) {
        dataStore.edit { prefs -> prefs[SESSION_ID_KEY] = id }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}
