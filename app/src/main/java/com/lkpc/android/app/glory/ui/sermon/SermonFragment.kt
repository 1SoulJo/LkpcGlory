package com.lkpc.android.app.glory.ui.sermon

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
import com.lkpc.android.app.glory.databinding.FragmentSermonBinding
import com.lkpc.android.app.glory.entity.BaseContent

class SermonFragment : Fragment() {

    private var _binding: FragmentSermonBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSermonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSermon.layoutManager = LinearLayoutManager(activity)
        binding.rvSermon.adapter = SermonAdapter()

        // data observation
        val viewModel: SermonViewModel by viewModels()
        val observer = Observer<List<BaseContent?>> { data ->
            val adapter = binding.rvSermon.adapter as SermonAdapter
            if (adapter.isLoading) {
                (adapter.sermons as MutableList<BaseContent?>).removeAt(adapter.sermons.size - 1)
                adapter.isLoading = false
            }
            adapter.sermons = data
            adapter.notifyDataSetChanged()
        }
        viewModel.getData().observe(activity as LifecycleOwner, observer)

        // setup refresh
        binding.sermonLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.sermonLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvSermon.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvSermon.adapter as SermonAdapter
                if (!binding.rvSermon.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.sermons as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.sermons.size - 1)
                    binding.rvSermon.scrollToPosition(adapter.sermons.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}