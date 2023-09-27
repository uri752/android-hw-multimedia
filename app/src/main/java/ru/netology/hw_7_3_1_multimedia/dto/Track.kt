package ru.netology.hw_7_3_1_multimedia.dto

data class Track(
    val id: Int,
    val file: String,
    var duration: String? ="",
    var isPlaying: Boolean = false
)