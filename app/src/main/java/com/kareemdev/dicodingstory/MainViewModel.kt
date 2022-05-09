package com.kareemdev.dicodingstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kareemdev.dicodingstory.data.local.datastore.LocalDataStore
import com.kareemdev.dicodingstory.data.local.entity.User
import kotlinx.coroutines.launch

class MainViewModel(private val pref:LocalDataStore) : ViewModel(){
    fun logout(){
        viewModelScope.launch {
            pref.logout()
        }
    }
    fun getUser(): LiveData<User> = pref.getUser().asLiveData()
}