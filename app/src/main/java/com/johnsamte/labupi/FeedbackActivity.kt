package com.johnsamte.labupi

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.johnsamte.labupi.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        binding.sendFeedbackButton.setOnClickListener {
            val title = binding.feedbackTitle.text.toString().trim()
            val body = binding.feedbackBody.text.toString().trim()

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                sendFeedback(title, body)
            }
        }
    }

    private fun sendFeedback(title: String, body: String) {
        val modifiedTitle = "Labupi: $title" // Prepend "Labupi" to the title
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("johnsamte28@gmail.com")) // Replace with your email
            putExtra(Intent.EXTRA_SUBJECT, modifiedTitle)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send Feedback"))

            binding.feedbackTitle.setText("")
            binding.feedbackBody.setText("")

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.feedbackToolbar)
        supportActionBar?.title = "Feedback Piakna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.feedbackToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}