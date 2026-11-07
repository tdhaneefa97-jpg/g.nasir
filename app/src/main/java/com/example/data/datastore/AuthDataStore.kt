package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "viswam_auth_prefs")

data class SavedSession(
    val id: String,
    val secretCode: String,
    val isAdmin: Boolean,
    val adminPasskey: String? = null,
    val rememberMe: Boolean = true
)

class AuthDataStore(private val context: Context) {

    companion object {
        val KEY_REMEMBER_ME = booleanPreferencesKey("key_remember_me")
        val KEY_SAVED_ID = stringPreferencesKey("key_saved_id")
        val KEY_SAVED_CODE = stringPreferencesKey("key_saved_code")
        val KEY_IS_ADMIN = booleanPreferencesKey("key_is_admin")
        val KEY_ADMIN_PASSKEY = stringPreferencesKey("key_admin_passkey")
    }

    val sessionFlow: Flow<SavedSession?> = context.authDataStore.data.map { prefs ->
        val remember = prefs[KEY_REMEMBER_ME] ?: false
        if (!remember) {
            null
        } else {
            val id = prefs[KEY_SAVED_ID] ?: ""
            val code = prefs[KEY_SAVED_CODE] ?: ""
            val isAdmin = prefs[KEY_IS_ADMIN] ?: false
            val passkey = prefs[KEY_ADMIN_PASSKEY]
            if (isAdmin && (!passkey.isNullOrBlank() && code.isNotBlank())) {
                SavedSession(
                    id = id.ifBlank { "ADMIN-001" },
                    secretCode = code,
                    isAdmin = true,
                    adminPasskey = passkey,
                    rememberMe = true
                )
            } else if (!isAdmin && id.isNotBlank() && code.isNotBlank()) {
                SavedSession(
                    id = id,
                    secretCode = code,
                    isAdmin = false,
                    rememberMe = true
                )
            } else {
                null
            }
        }
    }

    suspend fun saveClassmateSession(studentId: String, code: String, rememberMe: Boolean) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_REMEMBER_ME] = rememberMe
            if (rememberMe) {
                prefs[KEY_SAVED_ID] = studentId.trim()
                prefs[KEY_SAVED_CODE] = code.trim()
                prefs[KEY_IS_ADMIN] = false
                prefs.remove(KEY_ADMIN_PASSKEY)
            } else {
                prefs.remove(KEY_SAVED_ID)
                prefs.remove(KEY_SAVED_CODE)
                prefs.remove(KEY_IS_ADMIN)
                prefs.remove(KEY_ADMIN_PASSKEY)
            }
        }
    }

    suspend fun saveAdminSession(passkey: String, code: String, rememberMe: Boolean) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_REMEMBER_ME] = rememberMe
            if (rememberMe) {
                prefs[KEY_SAVED_ID] = "ADMIN-001"
                prefs[KEY_SAVED_CODE] = code.trim()
                prefs[KEY_IS_ADMIN] = true
                prefs[KEY_ADMIN_PASSKEY] = passkey.trim()
            } else {
                prefs.remove(KEY_SAVED_ID)
                prefs.remove(KEY_SAVED_CODE)
                prefs.remove(KEY_IS_ADMIN)
                prefs.remove(KEY_ADMIN_PASSKEY)
            }
        }
    }

    suspend fun clearSession() {
        context.authDataStore.edit { prefs ->
            prefs[KEY_REMEMBER_ME] = false
            prefs.remove(KEY_SAVED_ID)
            prefs.remove(KEY_SAVED_CODE)
            prefs.remove(KEY_IS_ADMIN)
            prefs.remove(KEY_ADMIN_PASSKEY)
        }
    }

    suspend fun getSavedSessionOnce(): SavedSession? {
        return sessionFlow.firstOrNull()
    }
}
