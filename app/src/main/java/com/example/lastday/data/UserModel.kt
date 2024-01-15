package com.example.lastday.data

data class UserModel(
    var id: String,
    val username: String?,
    val name: String?,
    val surname: String?,
    val profilePicture: String?,
    val about: String?,
    val savedPosts: List<String>
) {
    constructor() : this("", "", "", "", "", "", listOf())
}




