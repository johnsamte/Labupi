package com.johnsamte.labupi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.johnsamte.labupi.databinding.ActivityBnlBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BnlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBnlBinding
    private lateinit var dbHelper: LabuDatabaseHelper
    private lateinit var bnlPagerAdapter: BnlPagerAdapter
    private var isFullscreen = false
    private var currentMenuItem: MenuItem? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBnlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("id", -1)
        val laNumber = intent.getStringExtra("la_number")?:""
        val laThulu = intent.getStringExtra("la_thulu")?:""

        // Initialize the database helper
        dbHelper = LabuDatabaseHelper(this)

        // Fetch the list of Labu objects from the database
        val biaknalaList = dbHelper.getBNL()

        dbHelper.saveRecentView(id, laNumber, laThulu, "BNL", timestamp = System.currentTimeMillis())

        // Set up the ViewPager2 with the adapter
        bnlPagerAdapter = BnlPagerAdapter(biaknalaList)
        binding.biaknaLaViewPager.adapter = bnlPagerAdapter

        // Jump to the correct page in ViewPager2
       val initialPosition = biaknalaList.indexOfFirst { it.la_number == laNumber }
        if (initialPosition != -1) {
            binding.biaknaLaViewPager.setCurrentItem(initialPosition, false)

        }

        setupToolbar()

        binding.biaknaLaViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateBookmarkIcon()
                }
            }
        )


        val gestureDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    toggleFullscreen()
                    return true
                }
            }
        )

        // Attach to ViewPager2's child
        binding.biaknaLaViewPager.getChildAt(0).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

    }

    private fun updateBookmarkIcon() {
        val currentPage = binding.biaknaLaViewPager.currentItem
        val currentLabu = bnlPagerAdapter.getItemAt(currentPage)
        currentMenuItem?.let {
            if (dbHelper.isPageBnlBookmarked(currentLabu.la_number)) {
                it.setIcon(R.drawable.icon_bookmark) // red
            } else {
                it.setIcon(R.drawable.icon_bookmark_add) // normal
            }
        }
    }



    private fun toggleFullscreen() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isFullscreen) {
            // Exit fullscreen
            supportActionBar?.show()
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            // Enter fullscreen
            supportActionBar?.hide()
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        isFullscreen = !isFullscreen
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.biaknalaToolbar)
        supportActionBar?.title = "Biakna Labupi"

        // Delay font application until layout is done
       // binding.biaknalaToolbar.post {
           // val savedTypeface = FontUtils.loadSavedFont(this)
            // Try to find the TextView for the toolbar title
            //for (i in 0 until binding.biaknalaToolbar.childCount) {
                //val view = binding.biaknalaToolbar.getChildAt(i)
                //if (view is TextView && view.text == "Biakna Labupi") {
                    //view.setTypeface(savedTypeface, Typeface.BOLD)
                    //break
                //}
            //}
       // }

        binding.biaknalaToolbar.setNavigationOnClickListener {
            val intent = Intent(this, ContentBnlActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.biaknalaToolbar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private val currentLabuData: LabuData
        get() {
            val position = binding.biaknaLaViewPager.currentItem
            return bnlPagerAdapter.getItemAt(position)
        }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bnlpnl_menu, menu)
        currentMenuItem = menu?.findItem(R.id.bookmark)
        updateBookmarkIcon() // ✅ refresh immediately after menu is created
        try {
            val method = menu?.javaClass?.getDeclaredMethod(
                "setOptionalIconsVisible", Boolean::class.javaPrimitiveType
            )
            method?.isAccessible = true
            method?.invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace() // fallback if reflection fails
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.export -> {
              val data = currentLabuData
                val file = PptxCreator.createPptx(this, data)

                Toast.makeText(this, "Saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()

                true
            }


            R.id.share -> {
                shareContent()
                true
            }
            R.id.bookmark -> {
                toggleBookmark(item)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun shareContent() {
        val currentPage = binding.biaknaLaViewPager.currentItem // Get the current page index
        val adapter = binding.biaknaLaViewPager.adapter as BnlPagerAdapter
        val shareText = adapter.getCleanedTextForSharing(currentPage) // Get cleaned text

        // Create the intent for sharing
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        // Show the share chooser
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

   // private fun addBookmark() {
        // Get the current page index and retrieve the corresponding Labu object
        //val currentPage = binding.biaknaLaViewPager.currentItem
       // val adapter = binding.biaknaLaViewPager.adapter as BnlPagerAdapter
       // val currentLabu = adapter.getItemAt(currentPage) // Guaranteed to return a Labu object

        // Extract necessary details for the bookmark
        //val biaknalaId = currentLabu.la_number
           // or keep it as String if DB column expects text
        //val bookmarkTitle = currentLabu.la_thulu
       // val createdAt = getCurrentTimestamp()

        // Attempt to add the bookmark
       // val isAdded = dbHelper.addBnlBookmark(biaknalaId, bookmarkTitle, createdAt)

        // Show toast message based on the result
        //if (isAdded) {
            //Toast.makeText(this, "Bookmark Ah Chiamteh!", Toast.LENGTH_SHORT).show()
          //  adapter.notifyItemChanged(currentPage)
       // } else {
          //  MaterialAlertDialogBuilder(this)
              //  .setTitle("Ki-Bookmark khin!!")
              //  .setPositiveButton("OK", null)
              //  .show()
        //}
    //}

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy | HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun toggleBookmark(menuItem: MenuItem) {
        val currentPage = binding.biaknaLaViewPager.currentItem
        val adapter = binding.biaknaLaViewPager.adapter as BnlPagerAdapter
        val currentLabu = adapter.getItemAt(currentPage)

        val biaknalaId = currentLabu.la_number
        val bookmarkTitle = currentLabu.la_thulu
        val createdAt = getCurrentTimestamp()

        if (dbHelper.isPageBnlBookmarked(biaknalaId)) {
            // ✅ Already bookmarked → remove
           // dbHelper.deleteBnlBookmark(biaknalaId)
            val intent = Intent(this, BookmarkActivity::class.java)
            intent.putExtra(BookmarkActivity.EXTRA_TAB_INDEX, 0)
            startActivity(intent)

            //menuItem.setIcon(R.drawable.icon_bookmark_add)

            //Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
        } else {
            // ✅ Not bookmarked → add
            if (dbHelper.addBnlBookmark(biaknalaId, bookmarkTitle, createdAt)) {
                menuItem.setIcon(R.drawable.icon_bookmark)
              //  adapter.notifyItemChanged(currentPage)
               // Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        updateBookmarkIcon() // refresh toolbar icon when returning from BookmarkActivity
    }

}
