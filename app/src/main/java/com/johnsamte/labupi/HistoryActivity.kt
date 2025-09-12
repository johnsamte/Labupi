package com.johnsamte.labupi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.johnsamte.labupi.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var dbHelper: LabuDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LabuDatabaseHelper(this)
        setupToolbar()
        val recentView = dbHelper.getRecent()
        val validRecentView = recentView.filter { it.la_number >= 1.toString() }
        historyAdapter = HistoryAdapter(validRecentView) { clickedItem ->
            val intent = when (clickedItem.labu_min) {
                "BNL" -> Intent(this, BnlActivity::class.java)
                "PNL" -> Intent(this, PnlActivity::class.java)
                else -> null
            }
            intent?.apply {
                putExtra("la_number", clickedItem.la_number)
                putExtra("la_thulu", clickedItem.la_thulu)
            }
            intent?.let { startActivity(it) }
        }

        binding.historyRecycler.layoutManager = LinearLayoutManager(this)
        binding.historyRecycler.adapter = historyAdapter
        historyAdapter.updateData(validRecentView)



    }

    private fun setupToolbar() {
        setSupportActionBar(binding.historyToolbar)
        supportActionBar?.title = "Labupi History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.historyToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Delay font application until layout is done
        //binding.historyToolbar.post {
           // val savedTypeface = FontUtils.loadSavedFont(this)
            // Try to find the TextView for the toolbar title
            //for (i in 0 until binding.historyToolbar.childCount) {
                //val view = binding.historyToolbar.getChildAt(i)
                //if (view is TextView && view.text == "Labupi Zonna") {
                    //view.setTypeface(savedTypeface, Typeface.BOLD)
                   // break
                //}
           // }
        //}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.hist_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.histClear -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Clear History")
                        .setMessage("History na nulmang nuam taktak hia?")
                        .setPositiveButton("Yes") { _, _ ->
                            dbHelper.clearRecentViews() // clear from DB
                            historyAdapter.updateData(emptyList()) // refresh adapter
                        }
                        .setNegativeButton("No", null)
                        .show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}