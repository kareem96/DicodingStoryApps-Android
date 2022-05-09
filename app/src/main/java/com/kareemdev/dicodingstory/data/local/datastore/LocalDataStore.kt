package com.kareemdev.dicodingstory.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kareemdev.dicodingstory.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataStore private constructor(private val dataStore: DataStore<Preferences>){
    companion object{
        @Volatile
        private var INSTANCE: LocalDataStore? = null
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): LocalDataStore{
            return INSTANCE ?: synchronized(this){
                val instance = LocalDataStore(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
    fun getUser(): Flow<User>{
        return dataStore.data.map { preferences ->
            User(
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveUser(user: User){
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[STATE_KEY] = user.isLogin
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = false
            preferences.remove(STATE_KEY)
        }
    }

    suspend fun login(){
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }
}