package com.lkpc.android.app.glory.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lkpc.android.app.glory.databinding.FragmentNewsBinding
import com.lkpc.android.app.glory.entity.BaseContent


class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvNews.layoutManager = LinearLayoutManager(activity)
        binding.rvNews.adapter = NewsAdapter()

        // data observation
        val viewModel: NewsViewModel by viewModels()
        val observer = Observer<List<BaseContent?>> { data ->
            val adapter = binding.rvNews.adapter as NewsAdapter
            if (adapter.isLoading) {
                (adapter.newsList as MutableList<BaseContent?>).removeAt(adapter.newsList.size - 1)
                adapter.isLoading = false
            }
            adapter.newsList = data
            adapter.notifyDataSetChanged()
        }
        viewModel.getData().observe(activity as LifecycleOwner, observer)

        // setup refresh
        binding.newsLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.newsLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvNews.adapter as NewsAdapter
                if (!binding.rvNews.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.newsList as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.newsList.size - 1)
                    binding.rvNews.scrollToPosition(adapter.newsList.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}