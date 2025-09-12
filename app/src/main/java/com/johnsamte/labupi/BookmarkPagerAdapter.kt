package com.johnsamte.labupi

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BookmarkPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentBiaknala()
            1 -> FragmentPathianNgaihla()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}