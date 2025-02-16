package com.lkpc.android.app.glory.ui.meditation_detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MedViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> MedDetailMainFragment()
            1 -> MedDetailPrayerFragment()
            else -> MedDetailQtFragment()
        }
    }
}