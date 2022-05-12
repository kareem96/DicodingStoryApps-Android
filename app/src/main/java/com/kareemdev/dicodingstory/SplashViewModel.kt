package com.kareemdev.dicodingstory

import androidx.lifecycle.ViewModel
import com.kareemdev.dicodingstory.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel(){
    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()
}