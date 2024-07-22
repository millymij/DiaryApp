package com.example.fragments

/**
 * A data class representing a diary entry.
 */

data class DiaryEntry(
    val id: Int,
    val date: String,
    val title: String?,
    val media: String?,
    val entry: String
)
