package com.kareemdev.dicodingstory.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kareemdev.dicodingstory.MainActivity
import com.kareemdev.dicodingstory.data.local.entity.Story
import com.kareemdev.dicodingstory.databinding.FragmentHomeBinding
import com.kareemdev.dicodingstory.presentation.adapter.LoadingAdapter
import com.kareemdev.dicodingstory.presentation.adapter.StoryListAdapter
import com.kareemdev.dicodingstory.presentation.add.AddStoryActivity
import com.kareemdev.dicodingstory.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalPagingApi
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private var token: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = requireActivity().intent.getStringExtra(MainActivity.EXTRA_TOKEN) ?: ""

        getStories()
        setRecyclerView()
        swipeRefreshLayout()

        binding?.fabCreateStory?.setOnClickListener {
            Intent(requireContext(), AddStoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }

    private fun swipeRefreshLayout() {
        binding?.swipeRefresh?.setOnRefreshListener {
            getStories()
        }
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        listAdapter = StoryListAdapter()
        listAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && listAdapter.itemCount < 1) || loadState.source.refresh is LoadState.Error) {
                binding?.apply {
                    tvNotFoundError.animateVisibility(true)
                    ivNotFoundError.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                binding?.apply {
                    tvNotFoundError.animateVisibility(false)
                    ivNotFoundError.animateVisibility(false)
                    rvStories.animateVisibility(true)
                }
            }
            binding?.swipeRefresh?.isRefreshing = loadState.source.refresh is LoadState.Loading

        }
        try {
            recyclerView = binding?.rvStories!!
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingAdapter {
                        listAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun getStories() {
        viewModel.getStories(token).observe(viewLifecycleOwner) { result ->
            updateRecyclerView(result)
        }
    }

    private fun updateRecyclerView(result: PagingData<Story>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitData(lifecycle, result)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}