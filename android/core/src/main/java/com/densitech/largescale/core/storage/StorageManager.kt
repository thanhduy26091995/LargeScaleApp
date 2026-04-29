package com.densitech.largescale.core.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Typed wrapper around [DataStore<Preferences>].
 *
 * Centralises all persistent key-value storage.
 * Consumers work with plain Kotlin types (String, Int, etc.)
 * without dealing with [Preferences.Key] boilerplate directly.
 *
 * Injected as a singleton — all modules share the same DataStore file.
 */
@Singleton
class StorageManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // ── Write ─────────────────────────────────────────────────────────────────

    suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    suspend fun putInt(key: String, value: Int) {
        dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    suspend fun putLong(key: String, value: Long) {
        dataStore.edit { it[longPreferencesKey(key)] = value }
    }

    // ── Read (suspend one-shot) ───────────────────────────────────────────────

    suspend fun getString(key: String): String? =
        dataStore.data.first()[stringPreferencesKey(key)]

    suspend fun getInt(key: String): Int? =
        dataStore.data.first()[intPreferencesKey(key)]

    suspend fun getBoolean(key: String): Boolean? =
        dataStore.data.first()[booleanPreferencesKey(key)]

    suspend fun getLong(key: String): Long? =
        dataStore.data.first()[longPreferencesKey(key)]

    // ── Read (reactive Flow) ──────────────────────────────────────────────────

    fun observeString(key: String): Flow<String?> =
        dataStore.data.map { it[stringPreferencesKey(key)] }

    fun observeBoolean(key: String): Flow<Boolean?> =
        dataStore.data.map { it[booleanPreferencesKey(key)] }

    fun observeInt(key: String): Flow<Int?> =
        dataStore.data.map { it[intPreferencesKey(key)] }

    // ── Delete ────────────────────────────────────────────────────────────────

    suspend fun remove(key: String) {
        dataStore.edit { prefs ->
            // Try removing as all known types; DataStore ignores absent keys
            prefs.remove(stringPreferencesKey(key))
            prefs.remove(intPreferencesKey(key))
            prefs.remove(booleanPreferencesKey(key))
            prefs.remove(longPreferencesKey(key))
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
