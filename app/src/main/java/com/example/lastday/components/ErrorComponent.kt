package com.example.lastday.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ErrorComponent(tryAgain: () -> Unit) {
    Column {
        Text(
            text = "A technical problem was encountered while sending the request.",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = tryAgain) {
            Text(text = "Try Again")
        }
    }
}