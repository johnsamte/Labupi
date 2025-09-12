package com.johnsamte.labupi

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.johnsamte.labupi.databinding.ItemNumberBinding

class LaAdapter(
    context: Context,
    private val laList: List<LaCategoryData>,   // 👈 now we pass objects
    private val onClick: (LaCategoryData) -> Unit
) : RecyclerView.Adapter<LaAdapter.LaViewHolder>() {

    inner class LaViewHolder(val binding: ItemNumberBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(item: LaCategoryData) {
                binding.laNumber.text = item.la_id

                binding.root.setOnClickListener {
                    onClick(item)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaViewHolder {
        val binding = ItemNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LaViewHolder, position: Int) {
        holder.bind(laList[position])
    }

    override fun getItemCount(): Int = laList.size
}

