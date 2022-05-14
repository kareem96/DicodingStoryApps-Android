package com.kareemdev.dicodingstory.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kareemdev.dicodingstory.databinding.LayoutStoryLoadingBinding

class LoadingAdapter (private val retry:() -> Unit): LoadStateAdapter<LoadingAdapter.LoadStateViewHolder>(){
    class LoadStateViewHolder(
        private val binding: LayoutStoryLoadingBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState){
            if(loadState is LoadState.Error){
                binding.errorMsg.text = "Unable to fetch stories data"
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = LayoutStoryLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding, retry)
    }
}