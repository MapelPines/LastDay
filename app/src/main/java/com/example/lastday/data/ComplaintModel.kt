package com.example.lastday.data

import com.google.firebase.Timestamp

data class ComplaintModel(
    var id: String,
    val dateTime: Timestamp,
    val imageUrl: String,
    val likedUsers: List<String>?,
    val message: String,
    val userId: String,
) {
    constructor() : this("", Timestamp.now(), "", listOf(), "", "")
}




