package com.johnsamte.labupi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemHistoryBinding

class HistoryAdapter (
    private var historyView: List<HistoryData>,
    private val onItemClick: (HistoryData) -> Unit, // Click listener for handling item clicks

) : RecyclerView.Adapter<HistoryAdapter.RecentViewHolder>() {

    inner class RecentViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recentView: HistoryData) {

            val context = binding.root.context
            //val savedFont = FontUtils.loadSavedFont(context)

            //binding.numberText.typeface = savedFont
            //binding.contentTitle.typeface = savedFont
            //binding.types.typeface = savedFont

            // Bind data to the views
            binding.laNumber.text = recentView.la_number
            binding.laThulu.text = recentView.la_thulu
            binding.labuMin.text = recentView.labu_min


            // Optional: Format the timestamp for display
            //val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            //.format(Date(recentView.timestamp))
            //binding.timestampText.text = formattedTime

            // Set click listener for the item
            binding.root.setOnClickListener {
                onItemClick(recentView) // Trigger the click listener with the current item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(historyView[position])
    }

    override fun getItemCount(): Int = historyView.size

    // Method to update the dataset and notify the adapter
    fun updateData(newRecentViews: List<HistoryData>) {
        val diffCallback = HistViewDiffCallBack(historyView, newRecentViews)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        historyView = newRecentViews
        diffResult.dispatchUpdatesTo(this)
    }
}