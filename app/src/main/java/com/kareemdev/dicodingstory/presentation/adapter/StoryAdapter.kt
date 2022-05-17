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
import com.bumptech.glide.Glide
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.databinding.ItemContentBinding
import com.kareemdev.dicodingstory.presentation.detail.DetailActivity
import com.kareemdev.dicodingstory.presentation.detail.DetailActivity.Companion.EXTRA_DETAIL
import com.kareemdev.dicodingstory.utils.setLocalDateFormat


class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, story: Story) {
            binding.apply {
                Glide.with(context)
                    .load(story.photoUrl)
                    .into(ivStoryImage)
                tvStoryUsername.text = story.name
                tvStoryDescription.text = story.description
                tvStoryDate.setLocalDateFormat(story.createdAt)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivStoryImage, "image"),
                            Pair(tvStoryUsername, "username"),
                            Pair(tvStoryDescription, "description"),
                            Pair(tvStoryDate, "date"),
                        )

                    Intent(context, DetailActivity::class.java).also {
                        it.putExtra(EXTRA_DETAIL, story)
                        context.startActivity(it, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder.itemView.context, getItem(position)!!)
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> =
            object : DiffUtil.ItemCallback<Story>() {
                override fun areItemsTheSame(
                    oldItem: Story,
                    newItem: Story
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Story,
                    newItem: Story
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}