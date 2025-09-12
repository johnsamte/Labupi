package com.johnsamte.labupi

data class CategoriesData(
    val id: Int,
    val Acategory_min: String,
    var laCategories: List<LaCategoryData> = emptyList(),  // children
    var isExpanded: Boolean = false
)
