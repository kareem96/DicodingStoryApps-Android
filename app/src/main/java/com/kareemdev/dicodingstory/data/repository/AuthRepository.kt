package com.kareemdev.dicodingstory.data.repository
import com.kareemdev.dicodingstory.data.local.AuthDataStore
import com.kareemdev.dicodingstory.data.remote.response.LoginResponse
import com.kareemdev.dicodingstory.data.remote.response.RegisterResponse
import com.kareemdev.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val authDataStore: AuthDataStore
) {
    /**
     * Handle login operation for the users by calling the related API
     *
     * @param email User's email
     * @param password User's password
     * @return Flow
     */
    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = apiService.userLogin(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Handle registration process for the users by calling the related API
     *
     * @param name User's full name
     * @param email User's email
     * @param password User's password
     * @return Flow
     */
    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = apiService.userRegister(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Save user's authentication token to the preferences
     *
     * @param token User's authentication token
     */
    suspend fun saveAuthToken(token: String) {
        authDataStore.saveAuthToken(token)
    }

    /**
     * Get the user's authentication token from preferences
     *
     * @return Flow
     */
    fun getAuthToken(): Flow<String?> = authDataStore.getAuthToken()
}