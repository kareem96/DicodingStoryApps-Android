package com.kareemdev.dicodingstory.presentation.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.databinding.ItemContentBinding
import com.kareemdev.dicodingstory.presentation.detail.DetailActivity
import com.kareemdev.dicodingstory.presentation.detail.DetailActivity.Companion.EXTRA_DETAIL
import com.kareemdev.dicodingstory.utils.setImageFromUrl
import com.kareemdev.dicodingstory.utils.setLocalDateFormat


class StoryListAdapter: PagingDataAdapter<Story, StoryListAdapter.ViewHolder> (DiffCallback){
    class ViewHolder(private val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {
                tvStoryUsername.text = story.name
                tvStoryDescription.text = story.description
                ivStoryImage.setImageFromUrl(context, story.photoUrl)
                tvStoryDate.setLocalDateFormat(story.createdAt)

                // On item clicked
                root.setOnClickListener {
                    // Set ActivityOptionsCompat for SharedElement
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivStoryImage, "story_image"),
                            Pair(tvStoryUsername, "username"),
                            Pair(tvStoryDate, "date"),
                            Pair(tvStoryDescription, "description")
                        )

                    Intent(context, DetailActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}