package com.kareemdev.dicodingstory.presentation.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.databinding.ItemContentBinding
import com.kareemdev.dicodingstory.utils.setImageFromUrl
import com.kareemdev.dicodingstory.utils.setLocalDateFormat

class StoryListAdapter: PagingDataAdapter<Story, StoryListAdapter.ViewHolder> (DiffCallback){
    companion object{
        val DiffCallback = object : DiffUtil.ItemCallback<Story>(){
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

        }
    }
    class ViewHolder (private val binding: ItemContentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(context:Context, story: Story){
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDescription.text = story.description
                ivStoryImage.setImageFromUrl(context, story.photoUrl)
                tvStoryDate.setLocalDateFormat(story.createdAt)
                root.setOnClickListener {
                    val optionCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        root.context as Activity,
                        Pair(ivStoryImage, "story_image"),
                        Pair(tvStoryUsername, "username"),
                        Pair(tvStoryDate, "date"),
                        Pair(tvStoryDescription, "description"),
                    )
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StoryListAdapter.ViewHolder, position: Int) {
        val story = getItem(position)
        if(story != null){
            holder.bind(holder.itemView.context, story)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListAdapter.ViewHolder {
        val binding = ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}