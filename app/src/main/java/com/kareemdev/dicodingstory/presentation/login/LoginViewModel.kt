package com.kareemdev.dicodingstory.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kareemdev.dicodingstory.data.remote.response.LoginResponse
import com.kareemdev.dicodingstory.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> =
        authRepository.userLogin(email, password)


    fun saveAuthToken(token: String) {
        viewModelScope.launch {
            authRepository.saveAuthToken(token)
        }
    }
}