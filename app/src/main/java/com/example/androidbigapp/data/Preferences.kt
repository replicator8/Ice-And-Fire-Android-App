package com.example.androidbigapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
val BACKUP_FILENAME_KEY = stringPreferencesKey("backup_filename")
val FILE_EXISTS_KEY = booleanPreferencesKey("file_exists")
val FILE_SIZE_KEY = longPreferencesKey("file_size")
val INTERNAL_BACKUP_EXISTS_KEY = booleanPreferencesKey("internal_backup_exists")
