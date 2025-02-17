package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentMeditationDetailBinding
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2

class MeditationDetailFragment : Fragment(R.layout.fragment_meditation_detail) {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: MedViewPagerAdapter

    private var _binding: FragmentMeditationDetailBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeditationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = binding.tabSelector
        viewPager = binding.viewPager
        adapter = MedViewPagerAdapter(activity as FragmentActivity)

        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        viewPager.registerOnPageChangeCallback(object: OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                tabLayout.getTabAt(position)?.select()
            }
        })

        val viewModel: MeditationViewModelV2 by activityViewModels()
        viewModel.currentModel.observe(viewLifecycleOwner) {
            updateContent(it)
        }
        viewModel.getData().observe(activity as LifecycleOwner) { data ->
            viewModel.setCurrentModel(data.first())
        }
    }

    private fun updateContent(model: MeditationV2?) {
        if (model == null) {
            return
        }
        // TODO: Update the fragment
        binding.titleText.text = model.title
        binding.medDate.text = model.scheduledDate
    }
}