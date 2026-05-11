package com.rve.systemmonitor.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T> DataStore<Preferences>.getValueFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
    data.map { preferences -> preferences[key] ?: defaultValue }

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    edit { preferences -> preferences[key] = value }
}

fun <T : Enum<T>> DataStore<Preferences>.getEnumFlow(key: Preferences.Key<String>, defaultValue: T, valueOf: (String) -> T): Flow<T> =
    data.map { preferences ->
        val name = preferences[key] ?: defaultValue.name
        runCatching { valueOf(name) }.getOrElse { defaultValue }
    }

suspend fun <T : Enum<T>> DataStore<Preferences>.setEnum(key: Preferences.Key<String>, value: T) {
    edit { preferences -> preferences[key] = value.name }
}
