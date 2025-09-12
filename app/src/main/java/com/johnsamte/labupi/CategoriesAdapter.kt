package com.johnsamte.labupi

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemCategoryMinBinding


class CategoriesAdapter(
    private var categories: List<CategoriesData>,
    private val context: Context
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private var expandedCategoryPosition: Int? = null

    inner class CategoryViewHolder(private val binding: ItemCategoryMinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoriesData, position: Int) {
            binding.idNumber.text = category.id.toString()
            binding.aThupi.text = category.Acategory_min

            val isExpanded = position == expandedCategoryPosition
            binding.catRecyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Expand/collapse on click
            binding.root.setOnClickListener {
                if (expandedCategoryPosition == position) {
                    // Collapse current
                    expandedCategoryPosition = null
                    notifyItemChanged(position)
                } else {
                    // Collapse previous, expand new
                    val previousExpanded = expandedCategoryPosition
                    expandedCategoryPosition = position
                    notifyItemChanged(previousExpanded ?: -1)
                    notifyItemChanged(position)

                    // Scroll the clicked item into view
                    val recyclerView = binding.root.parent as? RecyclerView
                    val layoutManager = recyclerView?.layoutManager as? LinearLayoutManager
                    layoutManager?.scrollToPositionWithOffset(position, 0)
                }
            }

            // Inside CategoriesAdapter -> onBindViewHolder
            if (isExpanded && binding.catRecyclerView.adapter == null) {
                val laAdapter = LaAdapter(context, category.laCategories) { laCategoryData ->
                    val intent = Intent(context, BnlActivity::class.java).apply {
                        putExtra("la_number", laCategoryData.la_id) // number
                        putExtra("la_thulu", laCategoryData.la_thulu) //")// title/thulu
                    }
                    context.startActivity(intent)
                }

                binding.catRecyclerView.layoutManager =
                    GridLayoutManager(context, calculateNoOfColumns(context))
                binding.catRecyclerView.adapter = laAdapter

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryMinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<CategoriesData>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    private fun calculateNoOfColumns(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val scalingFactor = 70 // Adjust this value to control the number of columns
        return (dpWidth / scalingFactor).toInt()
    }
}


