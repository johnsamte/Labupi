package com.johnsamte.labupi

data class ChangelogItem(
    val id: String,        // GitHub release id
    val title: String,     // Release title
    val date: String,      // Published date
    val body: String,      // Release notes
    val downloadUrl: String
)

