package com.example.lastday.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lastday.data.ComplaintModel
import com.example.lastday.data.UserModel


@Composable
fun ComplaintItem(
    complaint: ComplaintModel,
    user: UserModel,
    isUserLiked: Boolean,
    isUserSaved: Boolean,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .shadow(
                elevation = 4.dp,
                spotColor = Color.Black,
                ambientColor = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.onTertiary)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileImage(
                url = user.profilePicture,
                size = DpSize(width = 42.dp, height = 42.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = "${user.username}")
        }

        Image(
            painter = rememberAsyncImagePainter(model = complaint.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )

        Column(
            Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LikeButton(isUserLiked = isUserLiked, onLikeClick)

                SaveButton(isUserSaved = isUserSaved, onSaveClick)
            }

            complaint.likedUsers.takeIf { (it?.size ?: 0) > 0 }?.let {
                Text(
                    text = "${it.size} user liked",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text(text = complaint.message, modifier = Modifier.padding(start = 8.dp))

            Text(
                text = complaint.dateTime.toDate().toString(),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}