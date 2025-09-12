package com.johnsamte.labupi

import androidx.recyclerview.widget.DiffUtil

class HistViewDiffCallBack (
    private val oldList: List<HistoryData>,
    private val newList: List<HistoryData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare items by unique identifier
        return oldList[oldItemPosition].la_number == newList[newItemPosition].la_number
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare the actual contents of the items
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}