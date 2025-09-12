package com.johnsamte.labupi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.johnsamte.labupi.databinding.ItemBookmarkBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.toString

class BookmarkAdapter(
    private var bookmarkList: List<BookmarkData>,
    private val onItemClick: (BookmarkData) -> Unit, // Expect a single BookmarkData, not a list
    private val onCancelClick: (BookmarkData) -> Unit,
    private val onEditClick: (BookmarkData) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    // ViewHolder with Binding
    inner class BookmarkViewHolder(private val binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmark: BookmarkData) {
            val context = binding.root.context
            //val savedFont = FontUtils.loadSavedFont(context)
           // binding.numbers.typeface = savedFont
            //binding.mainTitleText.typeface = savedFont
            //binding.timeStamp.typeface = savedFont


            binding.apply {
                numbers.text = bookmark.la_number
                // Append note under la_thulu if it exists
                mainTitleText.text = bookmark.la_thulu
                timeStamp.text = bookmark.createdAt // Ensure `createdAt` is displayed as a string

                if (!bookmark.note.isNullOrEmpty()) {
                    noteText.text = noteText.context.getString(R.string.bookmark_note, bookmark.note)
                    noteText.visibility = View.VISIBLE
                } else {
                    noteText.visibility = View.GONE
                }

                // Set click listener to pass the current item
                binding.root.setOnClickListener {
                    onItemClick(bookmark) // Pass the current bookmark item

                }

                binding.cancelBtn.setOnClickListener { view ->
                    val popupMenu = PopupMenu(view.context, view)
                    popupMenu.menuInflater.inflate(R.menu.bookmark_popup_menu, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_edit -> {
                                // Handle edit bookmark
                                onEditClick(bookmark)
                                true
                            }
                            R.id.action_delete -> {
                                // Show confirmation before delete
                                MaterialAlertDialogBuilder(view.context)
                                    .setTitle("Delete Bookmark!!")
                                    .setMessage("Are you sure you want to delete this bookmark?")
                                    .setPositiveButton("Delete") { _, _ ->
                                        onCancelClick(bookmark) // your delete function
                                    }
                                    .setNegativeButton("Cancel", null)
                                    .show()
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.show()
                }



            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(bookmarkList[position])
    }

    override fun getItemCount(): Int = bookmarkList.size

    fun updateData(newBookmarkList: List<BookmarkData>) {
        val diffCallback = BookmarkDiffCallback(bookmarkList, newBookmarkList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        bookmarkList = newBookmarkList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun formatStoredDate(storedDate: String): String {
        try {
            // Convert the stored date (string) into a Long and then to Date
            val timestamp = storedDate.toLong()
            val date = Date(timestamp)

            // Format the date
            val dateFormat = SimpleDateFormat("dd MMM yyyy | h:mm a", Locale.getDefault())
            return dateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            // Return a fallback value in case of an error
            return "Unknown Date & Time"
        }
    }
}