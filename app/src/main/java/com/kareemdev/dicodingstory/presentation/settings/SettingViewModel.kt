package com.kareemdev.dicodingstory.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kareemdev.dicodingstory.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel(){
    fun saveAuthToken(token:String){
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }
}