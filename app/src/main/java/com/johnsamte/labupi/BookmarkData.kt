package com.johnsamte.labupi

data class BookmarkData(
    val id: Int,
    val la_number: String,
    val la_thulu: String,
    val createdAt: String,
    var note: String? = null
)
