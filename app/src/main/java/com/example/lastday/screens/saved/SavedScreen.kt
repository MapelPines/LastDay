package com.example.lastday.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lastday.components.ComplaintItem
import com.example.lastday.components.ErrorComponent
import com.example.lastday.components.LoadingComponent
import com.example.lastday.data.ComplaintModel
import com.example.lastday.data.UserModel
import com.example.lastday.screens.MainViewModel
import com.example.lastday.screens.SavedScreenState

@Composable
fun SavedScreen(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getUserForSavedScreen()
    }
    when (val state = viewModel.savedScreenState) {
        is SavedScreenState.Content -> {
            SavedScreenContent(
                users = state.users,
                complaints = state.complaints,
                currentUserId = state.currentUserId,
                onSaveClick = viewModel::updateSaveStatusForSavedScreen,
                onLikeClick = viewModel::updateLikeStatusForSavedScreen
            )
        }

        SavedScreenState.Error -> ErrorComponent {
            viewModel.getUserForSavedScreen()
        }

        SavedScreenState.Loading -> LoadingComponent()
    }
}

@Composable
fun SavedScreenContent(
    users: List<UserModel>?,
    complaints: List<ComplaintModel>?,
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
                        isUserSaved = true,
                        onSaveClick = { onSaveClick.invoke(item.id) },
                        onLikeClick = { onLikeClick.invoke(item.id, item.likedUsers.orEmpty()) })

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}