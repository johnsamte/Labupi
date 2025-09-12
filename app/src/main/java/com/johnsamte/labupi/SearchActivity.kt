package com.johnsamte.labupi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnsamte.labupi.databinding.ActivitySearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var dbHelper: LabuDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        dbHelper = LabuDatabaseHelper(this)

        // Initialize RecyclerView and Adapter
        searchAdapter = SearchAdapter(this,emptyList()) { clickedItem ->
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


        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchResultsRecyclerView.adapter = searchAdapter

        // Setup live search
        setupLiveSearch()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.searchToolbars)
        supportActionBar?.title = "Labupi Zonna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.searchToolbars.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupLiveSearch() {
        binding.searchText.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null
            private val debounceDelay = 300L // 300ms debounce

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val searchTerm = s.toString().trim()

                // Cancel previous search
                searchJob?.cancel()

                if (searchTerm.isEmpty()) {
                    searchAdapter.updateData(emptyList())
                    return
                }

                // Launch coroutine with debounce
                searchJob = lifecycleScope.launch {
                    delay(debounceDelay)
                    binding.progressBar.visibility = View.VISIBLE
                    try {
                        val results = withContext(Dispatchers.IO) {
                            dbHelper.searchLabu(searchTerm) // BNL + PNL search
                        }
                        searchAdapter.updateData(results, searchTerm)
                    } finally {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        })
    }

}
