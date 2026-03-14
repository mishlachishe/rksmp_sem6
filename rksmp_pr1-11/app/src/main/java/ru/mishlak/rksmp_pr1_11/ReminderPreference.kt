package ru.mishlak.rksmp_pr1_11

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("reminder_prefs")

class ReminderPreferences(private val context: Context) {
	private val IS_ENABLED_KEY = booleanPreferencesKey("is_enabled")

	val isEnabledFlow: Flow<Boolean> = context.dataStore.data
		.map { preferences -> preferences[IS_ENABLED_KEY] ?: false }

	suspend fun setEnabled(enabled: Boolean) {
		context.dataStore.edit { preferences ->
			preferences[IS_ENABLED_KEY] = enabled
		}
	}
}