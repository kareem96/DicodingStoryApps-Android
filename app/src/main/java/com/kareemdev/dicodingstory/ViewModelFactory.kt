package com.kareemdev.dicodingstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kareemdev.dicodingstory.data.local.datastore.LocalDataStore
import com.kareemdev.dicodingstory.presentation.login.LoginViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private  val pref: LocalDataStore) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {MainViewModel(pref) as T}
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {LoginViewModel(pref) as T}
            else -> throw IllegalArgumentException("Inknownd ViewModel Class: " + modelClass.name)
        }
    }
}