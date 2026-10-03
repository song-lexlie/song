package com.song.bookshelf.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.song.bookshelf.core.data.model.SpineDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val spineDefault: Flow<SpineDefault> = dataStore.data.map { prefs ->
        prefs[SPINE_DEFAULT]
            ?.let { name -> SpineDefault.entries.firstOrNull { it.name == name } }
            ?: SpineDefault.BLANK
    }

    suspend fun setSpineDefault(value: SpineDefault) {
        dataStore.edit { it[SPINE_DEFAULT] = value.name }
    }

    private companion object {
        val SPINE_DEFAULT = stringPreferencesKey("spine_default")
    }
}
