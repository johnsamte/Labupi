package com.johnsamte.labupi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.johnsamte.labupi.databinding.ActivityBookmarkBinding

class BookmarkActivity: AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkBinding

    companion object {
        const val EXTRA_TAB_INDEX = "TAB_INDEX"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val pagerAdapter = BookmarkPagerAdapter(this)
        binding.bookmarkViewPager.adapter = pagerAdapter
        //val savedFont = FontUtils.loadSavedFont(this)
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.bookmarkTabLayout, binding.bookmarkViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "BIAKNA LABUPI"
                1 -> tab.text = "PATHIAN NGAIHLA"
            }
        }.attach()
        val tabIndex = intent.getIntExtra(EXTRA_TAB_INDEX, 0)
        binding.bookmarkViewPager.post {
            binding.bookmarkViewPager.currentItem = tabIndex
        }


    }

    private fun setupToolbar() {
        setSupportActionBar(binding.bookmarkToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.bookmarkToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.bookmarkToolbar.title = "Labupi Bookmark"

        //binding.bookmarkToolbar.post {
           // val savedTypeface = FontUtils.loadSavedFont(this)
            // Try to find the TextView for the toolbar title
            //for (i in 0 until binding.bookmarkToolbar.childCount) {
               // val view = binding.bookmarkToolbar.getChildAt(i)
               // if (view is TextView && view.text == "Bookmark") {
                  //  view.setTypeface(savedTypeface, Typeface.BOLD)
                   // break
                //}
            //}
        //}
    }
}