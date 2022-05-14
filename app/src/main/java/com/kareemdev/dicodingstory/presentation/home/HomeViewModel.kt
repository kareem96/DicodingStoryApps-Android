package com.kareemdev.dicodingstory.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
): ViewModel(){
    fun getStories(token:String): LiveData<PagingData<Story>> =
        storyRepository.getStories(token).cachedIn(viewModelScope).asLiveData()
}