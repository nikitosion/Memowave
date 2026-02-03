package com.memowave.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.memowave.app.core.security.CryptoManager
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager
) {
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    val token: Flow<String?> = dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]?.let { encrypted ->
            cryptoManager.decryptString(encrypted)
        }
    }

    fun getTokenSync(): String? = runBlocking { token.first() }

    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = cryptoManager.encryptString(token)
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}