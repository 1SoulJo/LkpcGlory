package com.lkpc.android.app.glory.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.lkpc.android.app.glory.BuildConfig
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.databinding.FragmentHomeBinding
import com.lkpc.android.app.glory.ui.basic_webview.BasicWebviewActivity
import com.lkpc.android.app.glory.ui.bulletin.BulletinActivity
import com.lkpc.android.app.glory.ui.news.NewsActivity
import com.lkpc.android.app.glory.ui.yt_channels.YoutubeChannelActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup view pager
        val viewModel: HomeViewModel by viewModels()
        viewModel.init(this)
        viewModel.getData().observe(requireActivity()) { ads ->
            viewModel.adapter.setData(ads.toMutableList())
            binding.homeViewPager.adapter = viewModel.adapter
            binding.homeViewPager.autoScroll(5000)

            TabLayoutMediator(binding.tabLayout, binding.homeViewPager) { _, _ ->
                binding.tabLayout.bringToFront()
            }.attach()
        }

        // setup grids
        binding.gridCenterLayout1.setOnClickListener {
            // LPC Live
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.LKPC_LIVE_VIDEO)))
        }
        binding.gridCenterLayout2.setOnClickListener {
            startActivity(Intent(requireContext(), YoutubeChannelActivity::class.java))
        }
        binding.gridCenterLayout3.setOnClickListener {
            // Bulletin
            if (BuildConfig.BULLETIN_SINGLE_ITEM_OPEN) {
                val i = Intent(requireContext(), BasicWebviewActivity::class.java)
                i.putExtra("title", R.string.bulletin_kr)
                i.putExtra("url", WebUrls.RECENT_BULLETIN)
                startActivity(i)
            } else {
                val i = Intent(requireContext(), BulletinActivity::class.java)
                startActivity(i)
            }
        }
        binding.gridCenterLayout4.setOnClickListener {
            // News
            val i = Intent(requireContext(), NewsActivity::class.java)
            i.putExtra("title", R.string.title_notifications)
            startActivity(i)
        }
        binding.gridCenterLayout5.setOnClickListener {
            // Newcomer registration
            val i = Intent(requireContext(), BasicWebviewActivity::class.java)
            i.putExtra("title", R.string.newcomer_reg_kr)
            i.putExtra("url", WebUrls.NEWCOMER_REG)
            startActivity(i)
        }
        binding.gridCenterLayout6.setOnClickListener {
            // Online offering
            val i = Intent(requireContext(), BasicWebviewActivity::class.java)
            i.putExtra("title", R.string.online_offering_kr)
            i.putExtra("url", WebUrls.ONLINE_OFFERING)
            startActivity(i)
        }
    }

    private fun ViewPager2.autoScroll(interval: Long) {
        val handler = Handler(requireActivity().mainLooper)
        var scrollPosition = 0

        val runnable = Runnable {
            val count = adapter?.itemCount ?: 0
            if (count > 0) {
                setCurrentItem((scrollPosition + 1) % count, true)
            }
        }

        registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scrollPosition = position

                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, interval)
                super.onPageSelected(position)
            }
        })

        handler.postDelayed(runnable, interval)
    }
}
