package com.johnsamte.labupi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnsamte.labupi.databinding.ActivityContentPnlBinding

class ContentPnlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentPnlBinding
    private lateinit var adapter: ContentPnlAdapter
    private var isSortedByTitle = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityContentPnlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val dbHelper = LabuDatabaseHelper(this)
        val pnlContent = dbHelper.getPNLContent()

        adapter = ContentPnlAdapter { clickedItem ->
            // Handle click and navigate to the new activity
            val intent = Intent(this, PnlActivity::class.java).apply {
                putExtra("la_number", clickedItem.la_number)
                putExtra("la_thulu", clickedItem.la_thulu)
            }
            startActivity(intent)
        }

        binding.pnlRecycler.layoutManager = LinearLayoutManager(this)
        binding.pnlRecycler.adapter = adapter

        // 🔑 Feed data into adapter
        adapter.submitFullList(pnlContent)
    }




    private fun setupToolbar() {
        setSupportActionBar(binding.contentPnlToolbar)
        supportActionBar?.title = "Content"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Set the toolbar's back button behavior
        binding.contentPnlToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Delay font application until layout is done
        // binding.contentBnlToolbar.post {
        // val savedTypeface = FontUtils.loadSavedFont(this)
        // Try to find the TextView for the toolbar title
        //for (i in 0 until binding.contentBnlToolbar.childCount) {
        // val view = binding.contentBnlToolbar.getChildAt(i)
        //if (view is TextView && view.text == "Content") {
        //   view.setTypeface(savedTypeface, Typeface.BOLD)
        // break
        // }
        //}
        //}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
     menuInflater.inflate(R.menu.content_menu, menu)
     val searchItem = menu?.findItem(R.id.contentSearch)
    val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

    // Set the query hint
    searchView?.queryHint = "La Number leh Thulu Zonna"

    searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
    // Perform final search (optional)
     return true
     }

    override fun onQueryTextChange(newText: String?): Boolean {
    // Filter the adapter based on query
      adapter.filter(newText.orEmpty())
      return true
    }
    })

     return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toggleBtn -> {
             toggleView(item)
             return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleView(menuItem: MenuItem) {
        if (isSortedByTitle) {
            // Sort by id (default order)
            adapter.sortItemsById()
             menuItem.setIcon(R.drawable.icon_list) // Change to List Icon
        } else {
            // Sort by mainTitle (A-Z order)
            adapter.sortItemsAlphabetically()
            menuItem.setIcon(R.drawable.icon_sort) // Change to Sort Icon
        }
        isSortedByTitle = !isSortedByTitle // Toggle the sorting state
    }


    // Function to calculate span count based on screen width
    private fun calculateDynamicSpanCount(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val scalingFactor = 60 // Adjust this value to control the number of columns
        return (dpWidth / scalingFactor).toInt()
    }

}