package com.johnsamte.labupi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnsamte.labupi.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: LabuDatabaseHelper
    private lateinit var adapter: CategoriesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LabuDatabaseHelper(this)

        lifecycleScope.launch {
            delay(3000) // 3s delay before showing main UI
        }

        setupToolbar()
        bnlButton()
        pnlButton()

        // 1. Load categories first
        val categories = dbHelper.getCategories()
        adapter = CategoriesAdapter(categories, this)
        binding.thupiRecycler.layoutManager = LinearLayoutManager(this)
        binding.thupiRecycler.adapter = adapter

        // 2. Load children for each category
        categories.forEach { laCategory ->
            laCategory.laCategories = dbHelper.getLaCategoriesByCategoryId(laCategory.id)
        }

        adapter.updateCategories(categories)


    }

    private fun setupToolbar() {
        setSupportActionBar(binding.mainToolbar)
        binding.mainToolbar.title = "Labupi"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about -> {

                true
            }

            R.id.search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.bookmark -> {
                val intent = Intent(this, BookmarkActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun bnlButton() {
        binding.biaknaLa.setOnClickListener {
            binding.biaknaLa.animate()
                .scaleX(0.9f) // Scale down slightly
                .scaleY(0.9f)
                .setDuration(100) // Animation duration in milliseconds
                .withEndAction {
                    binding.biaknaLa.animate()
                        .scaleX(1f) // Restore to original size
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            val intent = Intent(this, ContentBnlActivity::class.java)
                            startActivity(intent)
                        }
                        .start()
                }
                .start()
        }

    }
    private fun pnlButton(){
        binding.pathianNgaihla.setOnClickListener {
            binding.pathianNgaihla.animate()
                .scaleX(0.9f) // Scale down slightly
                .scaleY(0.9f)
                .setDuration(100) // Animation duration in milliseconds
                .withEndAction {
                    binding.pathianNgaihla.animate()
                        .scaleX(1f) // Restore to original size
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            // Start the activity after the animation
                            val intent = Intent(this, ContentPnlActivity::class.java)
                            startActivity(intent)
                        }
                        .start()
                }
                .start()
        }
    }
}