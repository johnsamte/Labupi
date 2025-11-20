package com.johnsamte.labupi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemContentBnlPnlBinding

class ContentPnlAdapter (
    private val onItemClick: (LabuContent) -> Unit
) : ListAdapter<LabuContent, ContentPnlAdapter.ContentPnlViewHolder>(DiffCallback()) {

    private var fullList: List<LabuContent> = emptyList() // Keep original list for filtering

    inner class ContentPnlViewHolder(private val binding: ItemContentBnlPnlBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contentPNL: LabuContent) {
            binding.laNumber.text = contentPNL.la_number
            binding.laThulu.text = contentPNL.la_thulu

            binding.root.setOnClickListener {
                onItemClick(contentPNL)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentPnlViewHolder {
        val binding = ItemContentBnlPnlBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContentPnlViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentPnlViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Set initial data
     */
    fun submitFullList(list: List<LabuContent>) {
        fullList = list
        submitList(list)
    }

    /**
     * Filtering
     */
    fun filter(query: String) {
        val filtered = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.la_thulu.contains(query, ignoreCase = true) ||
                        it.la_number.contains(query, ignoreCase = true)
            }
        }
        submitList(filtered)
    }

    /**
     * Sorting
     */
    fun sortItemsAlphabetically() {
        submitList(currentList.sortedBy { it.la_thulu })
    }

    fun sortItemsById() {
        submitList(currentList.sortedBy { it.id })
    }

    /**
     * DiffUtil callback
     */
    class DiffCallback : DiffUtil.ItemCallback<LabuContent>() {
        override fun areItemsTheSame(oldItem: LabuContent, newItem: LabuContent): Boolean {
            return oldItem.id == newItem.id // unique identifier
        }

        override fun areContentsTheSame(oldItem: LabuContent, newItem: LabuContent): Boolean {
            return oldItem == newItem // data class handles equality
        }
    }

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(johnsamte: RecyclerView) {
        super.onAttachedToRecyclerView(johnsamte)
        recyclerView = johnsamte
    }

    override fun onCurrentListChanged(
        previousList: MutableList<LabuContent>,
        currentList: MutableList<LabuContent>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        recyclerView?.scrollToPosition(0)
    }


}