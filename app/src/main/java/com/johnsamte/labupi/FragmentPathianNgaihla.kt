package com.johnsamte.labupi

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.johnsamte.labupi.databinding.FragmentBookmarkPathianNgaihlaBinding

class FragmentPathianNgaihla: Fragment() {
    private var _binding: FragmentBookmarkPathianNgaihlaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookmarkAdapter
    private lateinit var dbHelper: LabuDatabaseHelper

    private lateinit var bookmarkList: MutableList<BookmarkData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkPathianNgaihlaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch bookmarks from the database
        dbHelper = LabuDatabaseHelper(requireContext())
        val bookmarkList = dbHelper.getAllPnlBookmarks().toMutableList()


        // Initialize adapter with the fetched bookmarkList and set it to RecyclerView
        adapter = BookmarkAdapter(bookmarkList, onItemClick =  { clickedItem ->
            // Handle click and navigate to the new activity
            val intent = Intent(requireContext(), PnlActivity::class.java).apply {
                putExtra("la_number", clickedItem.la_number)
                putExtra("la_thulu", clickedItem.la_thulu)
            }
            startActivity(intent)
        },
            onCancelClick = { bookmark ->
                // Handle cancel button click and delete from database
                dbHelper.deletePnlBookmark(bookmark.la_number)

                val position = bookmarkList.indexOf(bookmark)
                if (position != -1) {
                    bookmarkList.removeAt(position) // Remove from list
                    adapter.notifyItemRemoved(position) // Notify RecyclerView about the removal
                }
            },
            onEditClick = { bookmark ->
                // ✅ Handle edit logic here
                // Example: show dialog to update bookmark name
                showEditDialog(bookmark)
            }
        )

        binding.pathianNgaihlaBMrecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.pathianNgaihlaBMrecycler.adapter = adapter

    }

    private fun showEditDialog(bookmark: BookmarkData) {
        val context = requireContext()

        val input = EditText(context).apply {
            setText(bookmark.note ?: "")
            hint = "Add a note"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setLines(3)
            setPadding(50, 40, 50, 10)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("Add Note")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newNote = input.text.toString().trim()
                // ✅ Update DB
                dbHelper.updatePnlNote(bookmark.la_number, newNote)

                // ✅ Update in memory
                val index = bookmarkList.indexOf(bookmark)
                if (index != -1) {
                    bookmarkList[index].note = newNote
                    adapter.notifyItemChanged(index)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}