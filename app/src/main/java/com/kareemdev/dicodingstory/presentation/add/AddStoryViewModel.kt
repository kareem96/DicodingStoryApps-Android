package com.kareemdev.dicodingstory.presentation.add

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.kareemdev.dicodingstory.data.remote.response.UploadResponse
import com.kareemdev.dicodingstory.data.repository.AuthRepository
import com.kareemdev.dicodingstory.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


@ExperimentalPagingApi
@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository,
):ViewModel(){
    fun getAuthToken(): Flow<String?> = authRepository.getAuthToken()

    suspend fun uploadImage(
        token:String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Flow<Result<UploadResponse>> = storyRepository.uploadStory(token, file, description, lat, lon)
}