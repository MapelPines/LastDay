package com.example.lastday.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.lastday.R


@Composable
fun ProfileImage(
    imageBitmap: Bitmap? = null,
    url: String? = null,
    size: DpSize = DpSize(width = 60.dp, height = 60.dp),
    onClick: (() -> Unit)? = null
) {
    val modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .clickable(enabled = onClick != null) { onClick?.invoke() }

    Box {
        imageBitmap?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = modifier
            )
        } ?: run {
            AsyncImage(
                model = url,
                error = painterResource(id = R.drawable.avatar),
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.avatar),
                contentScale = ContentScale.FillBounds,
                modifier = modifier
            )
        }

        onClick?.let {
            Image(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp)
                    .size(50.dp)

            )
        }
    }

}