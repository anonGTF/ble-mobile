package com.jamal.blescanner.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jamal.blescanner.utils.TAB_TITLES

class SectionPagerAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = TAB_TITLES.size
    override fun createFragment(position: Int): Fragment = DeviceListFragment(TAB_TITLES[position])
}