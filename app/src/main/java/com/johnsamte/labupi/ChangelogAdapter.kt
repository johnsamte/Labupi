package com.johnsamte.labupi

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemChangelogBinding

class ChangelogAdapter : RecyclerView.Adapter<ChangelogAdapter.ChangelogViewHolder>() {

    private val items = mutableListOf<ChangelogItem>()

    inner class ChangelogViewHolder(private val binding: ItemChangelogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChangelogItem) {
            binding.releaseTitle.text = item.title
            binding.releaseDate.text = item.date
            binding.releaseBody.text = item.body
            if (item.downloadUrl.isNotEmpty()) {
                binding.releaseDownload.apply {
                    text = context.getString(R.string.download)
                    isEnabled = true
                    alpha = 1.0f
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, item.downloadUrl.toUri())
                        it.context.startActivity(intent)
                    }
                }
            } else {
                    binding.releaseDownload.apply {
                        text = context.getString(R.string.no_download_file)
                        isEnabled = false
                        alpha = 0.5f
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangelogViewHolder {
        val binding = ItemChangelogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChangelogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChangelogViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: List<ChangelogItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = items.size
            override fun getNewListSize() = newItems.size

            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                items[oldPos].id == newItems[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean =
                items[oldPos] == newItems[newPos]
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}

