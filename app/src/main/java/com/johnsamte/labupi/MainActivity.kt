package com.johnsamte.labupi

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.johnsamte.labupi.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.net.URL

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
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)

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

            R.id.update -> {
                checkUpdates(this)

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

    private fun checkUpdates(context: Context) {
        val apiUrl = "https://api.github.com/repos/johnsamte/Labupi/releases/latest"

        Thread {
            try {
                val jsonStr = URL(apiUrl).readText()
                val jsonObj = JSONObject(jsonStr)

                // Extract info from JSON
                val latestTag = jsonObj.getString("tag_name") // e.g. "v2.4"
                val latestVersion = latestTag.removePrefix("v")
                val whatsNew = jsonObj.getString("body")

                val assets = jsonObj.getJSONArray("assets")
                val apkUrl = assets.getJSONObject(0).getString("browser_download_url")

                // Current version from app
                val currentVersion = context.packageManager
                    .getPackageInfo(context.packageName, 0).versionName

                (context as Activity).runOnUiThread {
                    if (latestVersion != currentVersion) {
                        showUpdateDialog(context, latestVersion, whatsNew, apkUrl)
                    } else {
                        MaterialAlertDialogBuilder(this)
                            .setIcon(R.mipmap.ic_launcher) // your app icon
                            .setTitle(getString(R.string.app_name))
                            .setMessage("YOUR APP IS UP-TO-DATE\n\nCurrent Install Version : v$currentVersion")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                (context as Activity).runOnUiThread {
                    MaterialAlertDialogBuilder(context)
                        .setIcon(R.mipmap.ic_launcher) // your app icon
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Update Check Failed ⚠️\n\nSomething went wrong while checking for updates. Please try again later.")
                        .setPositiveButton("AHIHLELAH", null)
                        .show()
                }
            }

        }.start()
    }

    private fun showUpdateDialog(
        context: Context,
        versionName: String,
        whatsNew: String,
        downloadUrl: String,
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Labupi App Update : (v$versionName)")
            .setMessage("Kikhek khenkhat:\n$whatsNew")
            .setPositiveButton("Download") { _, _ ->
                downloadAndInstallApk(context, downloadUrl)
            }
            .setNegativeButton("Later", null)
            .show()
    }

    @SuppressLint("Range")
     fun downloadAndInstallApk(context: Context, apkUrl: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        // 1️⃣ Cancel any existing active downloads for our file
        val query = DownloadManager.Query()
        val cursor = downloadManager.query(query)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if (title == "Downloading Update" &&
                    (status == DownloadManager.STATUS_PENDING ||
                            status == DownloadManager.STATUS_RUNNING ||
                            status == DownloadManager.STATUS_PAUSED)
                ) {
                    val id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
                    downloadManager.remove(id) // cancel old download
                }
            }
            cursor.close()
        }

        // 2️⃣ Scoped storage safe path (private to app, no duplicate names)
        val downloadFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "labupi_update.apk"
        )
        if (downloadFile.exists()) {
            downloadFile.delete()
        }

        // 3️⃣ Use setDestinationUri (prevents -1, -2, …)
        val request = DownloadManager.Request(apkUrl.toUri())
            .setTitle("Downloading Update")
            .setDescription("Ngak hamham ni aki download sung..")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(downloadFile)) // ✅ overwrite only
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadId = downloadManager.enqueue(request)

        // Inline UI
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val progressBar =
            ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = 0
                isIndeterminate = false
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

        val statusText = TextView(context).apply {
            text = context.getString(R.string.preparing_download)
            setPadding(0, 20, 0, 0)
        }

        layout.addView(progressBar)
        layout.addView(statusText)

        // Dialog (cannot be dismissed by back or outside touch)
        val dialog = MaterialAlertDialogBuilder(context)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(getString(R.string.app_name))
            .setMessage("Downloading update")
            .setView(layout)
            .setCancelable(false)
            .setNegativeButton("Cancel") { d, _ ->
                downloadManager.remove(downloadId) // explicit cancel
                d.dismiss()
            }
            .show()

        dialog.setCanceledOnTouchOutside(false)
       // dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
           // ContextCompat.getColor(this, R.color.background)
        //)

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)

                if (cursor != null && cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val bytesDownloaded =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    if (bytesTotal > 0) {
                        val progress = ((bytesDownloaded * 100L) / bytesTotal).toInt()
                        progressBar.progress = progress
                        statusText.text = context.getString(R.string.downloading_progress, progress)
                    }

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            handler.removeCallbacks(this)
                            cursor.close()

                            if (dialog.isShowing) {
                                dialog.dismiss()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val apkUri = downloadManager.getUriForDownloadedFile(downloadId)

                                    if (apkUri != null) {
                                        val notificationManager =
                                            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                                        notificationManager.cancel(downloadId.toInt())

                                        val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(apkUri, "application/vnd.android.package-archive")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        try {
                                            context.startActivity(installIntent)
                                        } catch (_: ActivityNotFoundException) {
                                            Toast.makeText(
                                                context,
                                                "Cannot launch installer",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Download URI not found",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }, 500)
                            }
                            return
                        }

                        DownloadManager.STATUS_FAILED -> {
                            handler.removeCallbacks(this)
                            cursor.close()
                            if (dialog.isShowing) dialog.dismiss()
                            Toast.makeText(context, "Download failed", Toast.LENGTH_LONG).show()
                            return
                        }
                    }
                }
                cursor?.close()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

}