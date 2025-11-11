package com.cs407.noteapp_v2.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class Sort(val sort: Int, val sortBy: String, val label: String) {
    NOTE_ID_DESC(0, "noteID", "Sort by Note ID descending"),
    NOTE_ID_ASC(1, "noteID", "Sort by Note ID ascending"),

    NOTE_TITLE_DESC(0, "noteTitle", "Sort by Note Title descending"),
    NOTE_TITLE_ASC(1, "noteTitle", "Sort by Note Title ascending"),

    LAST_EDITED_DESC(0, "lastEdited", "Sort by Last Edited descending"),
    LAST_EDITED_ASC(1, "lastEdited", "Sort by Last Edited ascending"),

    // ✅ fixed: ASC uses 1, DESC uses 0
    PRIORITY_DESC(0, "priority", "Sort by Priority descending"),
    PRIORITY_ASC(1, "priority", "Sort by Priority ascending"),

    REMIND_DESC(0, "remindDate", "Sort by Reminding Date descending"),
    REMIND_ASC(1, "remindDate", "Sort by Reminding Date ascending");
}

@Serializable
data class AppPreferences (
    var greeting: String = "Welcome",
    var sorting: Sort = Sort.LAST_EDITED_DESC
    // TODO: milestone 1 step 9-1
)

class PreferenceKV(private val context: Context, private val userUID: String) {
    companion object {
        private val Context.dataStore by preferencesDataStore("preferenceKV")

        private object PreferenceKeys {
            const val GREETING = "greeting"
            const val SORTING = "sorting"
        }
    }
    private val userUIDPre = stringPreferencesKey(userUID)

    val appPreferencesFlow = context.dataStore.data.map { preferences ->
        val jsonString = preferences[userUIDPre] ?: """
        {
          "greeting": "Welcome",
          "sorting": ${Json.encodeToString(Sort.LAST_EDITED_DESC)}
        }
    """.trimIndent()
        Json.decodeFromString<AppPreferences>(jsonString)
    }
    suspend fun saveGreeting(greeting: String) {
        context.dataStore.edit { preferences ->
            val jsonString = preferences[userUIDPre] ?: "{}"
            val appPrefer: AppPreferences = Json.decodeFromString(jsonString)
            appPrefer.greeting = greeting
            preferences[userUIDPre] = Json.encodeToString(AppPreferences.serializer(), appPrefer)
        }
    }

    suspend fun saveSorting(sort: Sort) {
        context.dataStore.edit { preferences ->
            val jsonString = preferences[userUIDPre] ?: Json.encodeToString(AppPreferences())
            val appPrefer  = Json.decodeFromString<AppPreferences>(jsonString)
            appPrefer.sorting = sort
            preferences[userUIDPre] = Json.encodeToString(AppPreferences.serializer(), appPrefer)
        }
    }
}