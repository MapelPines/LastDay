package com.example.lastday.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LikeButton(isUserLiked: Boolean, onLikeClick: () -> Unit) {
    val icon = if (isUserLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    val color = if (isUserLiked) Color.Red else Color.Black

    IconButton(onClick = onLikeClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(36.dp)
        )
    }
}