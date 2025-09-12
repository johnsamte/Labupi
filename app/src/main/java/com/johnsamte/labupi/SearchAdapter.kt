package com.johnsamte.labupi

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemSearchBinding

class SearchAdapter(
    private val context: Context,
    private var searchResult: List<SearchData>,
    private val onItemClick: (SearchData) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var query: String? = null // store current search term

    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchData) {
            binding.numberText.text = item.la_number
            binding.contentTitle.text = query?.let { highlightQuery(item.la_thulu, it) } ?: item.la_thulu
            binding.types.text = item.labu_min

            // Replace \n with space
            val snippetText = item.la_text1?.replace("\\n", " ") ?: ""
            val snippetChorus = item.chorus1?.replace("\\n", " ") ?: ""

            // Highlight query if available
            binding.snippetText.apply {
                text = query?.let { highlightQuery(snippetText, it) } ?: snippetText.truncate(50)
                visibility = if (snippetText.isNotEmpty()) View.VISIBLE else View.GONE
            }

            binding.snippetChorus.apply {
                text = query?.let { highlightQuery(snippetChorus, it) } ?: snippetChorus.truncate(50)
                visibility = if (snippetChorus.isNotEmpty()) View.VISIBLE else View.GONE
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    private fun String.truncate(maxLength: Int): String {
        return if (this.length > maxLength) this.take(maxLength) + "…" else this
    }

    private fun highlightQuery(text: String, query: String): SpannableString {
        val truncatedText = text.truncate(50) // truncate first
        val spannable = SpannableString(truncatedText)

        val startIndex = truncatedText.indexOf(query, ignoreCase = true)
        val highlightColor = ContextCompat.getColor(context, R.color.textHighlight)// search in truncated
        if (startIndex >= 0) {
            spannable.setSpan(
                ForegroundColorSpan(highlightColor),
                startIndex,
                startIndex + query.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchResult[position])
    }

    override fun getItemCount(): Int = searchResult.size

    fun updateData(newData: List<SearchData>, searchQuery: String? = null) {
        this.query = searchQuery // store current query
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = searchResult.size
            override fun getNewListSize() = newData.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                searchResult[oldItemPosition].id == newData[newItemPosition].id
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                searchResult[oldItemPosition] == newData[newItemPosition]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        searchResult = newData
        diffResult.dispatchUpdatesTo(this)
    }
}

