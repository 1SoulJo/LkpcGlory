package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
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
    private lateinit var dataList: List<MeditationV2>
    private var currentIndex: Int = 0

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
//                tab.view.setBackgroundColor(resources.getColor(R.color.selected_tab_bg))
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
//                tab.view.setBackgroundColor(resources.getColor(R.color.tab_bg))
            }

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
            if (!this@MeditationDetailFragment::dataList.isInitialized) {
                return@observe
            }
            currentIndex = dataList.indexOf(it)
            updateContent(it)
        }
        viewModel.getData().observe(activity as LifecycleOwner) { data ->
            dataList = data
            dataList.forEach {
                it.scheduledDate?.let { date ->
                    viewModel.dataMap[date] = it
                }
            }
            viewModel.setCurrentModel(data[currentIndex])
        }

        binding.todayBtn.setOnClickListener {
            if (currentIndex == 0) {
                return@setOnClickListener
            }
            currentIndex = 0
            viewModel.setCurrentModel(dataList[currentIndex])
        }
        binding.dateLeft.setOnClickListener {
            if (dataList.size <= currentIndex + 1) {
                return@setOnClickListener
            }
            currentIndex += 1
            viewModel.setCurrentModel(dataList[currentIndex])
        }
        binding.dateRight.setOnClickListener {
            if (currentIndex <= 0) {
                return@setOnClickListener
            }
            currentIndex -= 1
            viewModel.setCurrentModel(dataList[currentIndex])
        }
        binding.calBtn.setOnClickListener {
            MedCalendarFragment().show(childFragmentManager, "MedCal")
        }
    }

    private fun updateContent(model: MeditationV2?) {
        if (model == null) {
            return
        }
        binding.titleText.text = model.title
        binding.medDate.text = model.scheduledDate
    }
}