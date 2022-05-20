package com.kareemdev.dicodingstory.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kareemdev.dicodingstory.data.local.AuthDataStore
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.data.local.room.StoryDatabase
import com.kareemdev.dicodingstory.data.remote.RemoteDataStory
import com.kareemdev.dicodingstory.data.remote.response.StoryResponse
import com.kareemdev.dicodingstory.data.remote.response.UploadResponse
import com.kareemdev.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val authDataStore: AuthDataStore

){

    private fun generateBearerToken(token: String): String {
        return "Bearer $token"
    }
    fun getStories(
        token: String
    ): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = RemoteDataStory(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }


    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Flow<Result<UploadResponse>> = flow {
        try {
            val tokenBearer = generateBearerToken(token)
            val response = apiService.uploadImage(tokenBearer, file, description, lat, lon)
            emit(Result.success(response))
        }catch (e:Exception){
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }


    fun getStoriesLocation(token: String): Flow<Result<StoryResponse>> = flow {
        try {
            val userToken = "Bearer $token"
            val response = apiService.getAllStories(userToken, null, size = 100, location = 1)
            emit(Result.success(response))
        }catch (exception: Exception){
            emit(Result.failure(exception))
        }
    }


    fun getToken(): Flow<String?> = authDataStore.getAuthToken()

}