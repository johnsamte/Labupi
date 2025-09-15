package com.johnsamte.labupi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.johnsamte.labupi.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    private lateinit var adapter: AboutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        adapter = AboutAdapter(this)
        binding.aboutPager.adapter = adapter
        TabLayoutMediator(binding.aboutTap, binding.aboutPager){ tab, position ->
            when (position) {
                0 -> tab.text = "About"
                1 -> tab.text = "Changelog"
            }
        }.attach()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.aboutToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.aboutToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.aboutToolbar.title = "About Labupi"

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