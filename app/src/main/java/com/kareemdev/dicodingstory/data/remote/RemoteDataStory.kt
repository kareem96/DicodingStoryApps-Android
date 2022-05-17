package com.kareemdev.dicodingstory.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.kareemdev.dicodingstory.data.local.entity.RemoteKeys
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.data.local.room.StoryDatabase
import com.kareemdev.dicodingstory.data.remote.retrofit.ApiService
import java.lang.Exception

@ExperimentalPagingApi
class RemoteDataStory(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val token: String,
): RemoteMediator<Int, Story>(){

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData = apiService.getAllStories(token, page, state.config.pageSize, null)
            val eofPaginationReached = responseData.storyResponseItems.isEmpty()

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (eofPaginationReached) null else page + 1
                val keys = responseData.storyResponseItems.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                database.remoteKeysDao().insertAll(keys)
                responseData.storyResponseItems.forEach {
                    val story = Story(
                        it.id,
                        it.name,
                        it.description,
                        it.photoUrl,
                        it.createdAt,
                        it.lat,
                        it.lon
                    )
                    database.storyDao().insertStory(story)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = eofPaginationReached)

        } catch (exception: Exception) {

            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}