package com.kareemdev.dicodingstory.presentation.register

import androidx.lifecycle.ViewModel
import com.kareemdev.dicodingstory.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository) :
    ViewModel() {
    suspend fun registerUser(name: String, email: String, password: String) =
        authRepository.userRegister(name, email, password)
}