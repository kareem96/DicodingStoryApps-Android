package com.kareemdev.dicodingstory.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kareemdev.dicodingstory.data.local.datastore.LocalDataStore
import com.kareemdev.dicodingstory.data.local.entity.User
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LocalDataStore) : ViewModel(){
    fun saveUser(user: User){
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}