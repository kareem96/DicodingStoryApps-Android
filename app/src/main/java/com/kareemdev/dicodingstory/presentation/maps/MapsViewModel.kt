package com.kareemdev.dicodingstory.presentation.maps

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.kareemdev.dicodingstory.data.remote.response.StoryResponse
import com.kareemdev.dicodingstory.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    fun getToken(): Flow<String?> = repository.getToken()

    fun getStoriesLocation(token: String): Flow<Result<StoryResponse>> =
        repository.getStoriesLocation(token)
}