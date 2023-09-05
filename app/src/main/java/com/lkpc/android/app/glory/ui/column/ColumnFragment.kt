package com.lkpc.android.app.glory.ui.column

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
import com.lkpc.android.app.glory.databinding.FragmentColumnBinding
import com.lkpc.android.app.glory.entity.BaseContent

class ColumnFragment : Fragment() {

    private var _binding: FragmentColumnBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentColumnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvColumn.layoutManager = LinearLayoutManager(activity)
        binding.rvColumn.adapter = ColumnAdapter()

        // data observation
        val viewModel: ColumnViewModel by viewModels()
        val observer = Observer<List<BaseContent?>> { data ->
            val adapter = binding.rvColumn.adapter as ColumnAdapter
            if (adapter.isLoading) {
                (adapter.columns as MutableList<BaseContent?>).removeAt(adapter.columns.size - 1)
                adapter.isLoading = false
            }
            adapter.columns = data
            adapter.notifyDataSetChanged()
        }
        viewModel.getData().observe(activity as LifecycleOwner, observer)

        // setup refresh
        binding.columnLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.columnLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvColumn.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvColumn.adapter as ColumnAdapter
                if (!binding.rvColumn.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.columns as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.columns.size - 1)
                    binding.rvColumn.scrollToPosition(adapter.columns.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}