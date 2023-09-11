package com.lkpc.android.app.glory.ui.fellow_news

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
import com.lkpc.android.app.glory.databinding.FragmentFellowNewsBinding
import com.lkpc.android.app.glory.entity.BaseContent

class FellowNewsFragment : Fragment() {

    private var _binding: FragmentFellowNewsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFellowNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFellowNews.layoutManager = LinearLayoutManager(activity)
        binding.rvFellowNews.adapter = FellowNewsAdapter()

        // data observation
        val viewModel: FellowNewsViewModel by viewModels()
        val observer = Observer<List<BaseContent?>> { data ->
            val adapter = binding.rvFellowNews.adapter as FellowNewsAdapter
            if (adapter.isLoading) {
                (adapter.fellowNews as MutableList<BaseContent?>).removeAt(adapter.fellowNews.size - 1)
                adapter.isLoading = false
            }
            adapter.fellowNews = data
            adapter.notifyDataSetChanged()
        }
        viewModel.getData().observe(activity as LifecycleOwner, observer)

        // setup refresh
        binding.fellowNewsLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.fellowNewsLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvFellowNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvFellowNews.adapter as FellowNewsAdapter
                if (!binding.rvFellowNews.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.fellowNews as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.fellowNews.size - 1)
                    binding.rvFellowNews.scrollToPosition(adapter.fellowNews.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}
