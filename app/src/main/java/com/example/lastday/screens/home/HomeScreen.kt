package com.example.lastday.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.lastday.components.ComplaintItem
import com.example.lastday.components.ErrorComponent
import com.example.lastday.components.LikeButton
import com.example.lastday.components.LoadingComponent
import com.example.lastday.components.ProfileImage
import com.example.lastday.components.SaveButton
import com.example.lastday.data.ComplaintModel
import com.example.lastday.data.UserModel
import com.example.lastday.screens.HomeScreenState
import com.example.lastday.screens.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getUserForHomeScreen()
    }

    when (val state = viewModel.homeScreenState) {
        is HomeScreenState.Content -> {
            HomeScreenContent(
                users = state.users,
                complaints = state.complaints,
                savedPosts = state.savedPosts,
                currentUserId = state.currentUserId,
                onSaveClick = viewModel::updateSaveStatusForHome,
                onLikeClick = viewModel::updateLikeStatusForHome
            )
        }

        HomeScreenState.Error -> ErrorComponent {
            viewModel.getUserForHomeScreen()
        }

        HomeScreenState.Loading -> LoadingComponent()
    }
}

@Composable
fun HomeScreenContent(
    users: List<UserModel>?,
    complaints: List<ComplaintModel>?,
    savedPosts: List<String>,
    currentUserId: String,
    onSaveClick: (complaintId: String) -> Unit,
    onLikeClick: (complaintId: String, likedUsers: List<String>) -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        ) {
            itemsIndexed(complaints.orEmpty()) { _, item ->
                val user = users?.find { it.id.equals(item.userId) }
                user?.let {
                    ComplaintItem(
                        complaint = item,
                        user = user,
                        isUserLiked = item.likedUsers?.any { it == currentUserId } ?: false,
                        isUserSaved = savedPosts.any { it == item.id },
                        onSaveClick = { onSaveClick.invoke(item.id) },
                        onLikeClick = { onLikeClick.invoke(item.id, item.likedUsers.orEmpty()) })

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
