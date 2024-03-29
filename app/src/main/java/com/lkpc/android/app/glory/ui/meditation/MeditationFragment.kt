package com.lkpc.android.app.glory.ui.meditation

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
import com.lkpc.android.app.glory.databinding.FragmentMeditationBinding
import com.lkpc.android.app.glory.entity.BaseContent

class MeditationFragment : Fragment() {

    private var _binding: FragmentMeditationBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeditationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMeditation.layoutManager = LinearLayoutManager(activity)
        binding.rvMeditation.adapter = MeditationAdapter()

        // data observation
        val viewModel: MeditationViewModel by viewModels()
        val observer = Observer<List<BaseContent?>> { data ->
            val adapter = binding.rvMeditation.adapter as MeditationAdapter
            if (adapter.isLoading) {
                (adapter.meditations as MutableList<BaseContent?>).removeAt(adapter.meditations.size - 1)
                adapter.isLoading = false
            }
            adapter.meditations = data
            adapter.notifyDataSetChanged()
        }
        viewModel.getData().observe(activity as LifecycleOwner, observer)

        // setup refresh
        binding.meditationLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.meditationLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvMeditation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvMeditation.adapter as MeditationAdapter
                if (!binding.rvMeditation.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.meditations as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.meditations.size - 1)
                    binding.rvMeditation.scrollToPosition(adapter.meditations.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}