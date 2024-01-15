package com.example.lastday.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lastday.components.ComplaintItem
import com.example.lastday.components.ErrorComponent
import com.example.lastday.components.LoadingComponent
import com.example.lastday.components.ProfileImage
import com.example.lastday.data.ComplaintModel
import com.example.lastday.data.UserModel
import com.example.lastday.screens.MainViewModel
import com.example.lastday.screens.ProfileScreenState

@Composable
fun ProfileScreen(viewModel: MainViewModel, navigateToSetting: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.getUserForProfile()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = viewModel.profileScreenState) {
            is ProfileScreenState.Content -> {
                ProfileContent(
                    state.user,
                    viewModel.numberOfComplaints,
                    navigateToSetting
                )

                state.complaints?.let {
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp), thickness = 2.dp
                    )

                    ComplaintsContent(
                        user = state.user,
                        complaints = state.complaints,
                        onSaveClick = viewModel::updateSaveStatusForProfileScreen,
                        onLikeClick = viewModel::updateLikeStatusForProfile
                    )
                }
            }

            ProfileScreenState.Error -> ErrorComponent {
                viewModel.getUserForProfile()
            }

            ProfileScreenState.Loading -> LoadingComponent()
        }
    }
}

@Composable
private fun ProfileContent(
    user: UserModel,
    numberOfComplaints: String,
    navigateToSetting: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text(
            text = "${user.name} ${user.surname}",
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileImage(
                url = user.profilePicture,
                size = DpSize(width = 100.dp, height = 100.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            ProfileStat(
                modifier = Modifier.weight(1f),
                numberText = numberOfComplaints,
                text = "Number Of Complaints"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = navigateToSetting) {
            Text(text = "Edit profile")
        }

        Text(text = user.about.orEmpty())
    }
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text)
    }
}

@Composable
fun ComplaintsContent(
    user: UserModel, complaints: List<ComplaintModel>,
    onSaveClick: (complaintId: String) -> Unit,
    onLikeClick: (complaintId: String, likedUsers: List<String>) -> Unit
) {

    Column(Modifier.padding(horizontal = 12.dp)) {
        complaints.forEachIndexed { _, item ->
            ComplaintItem(
                complaint = item,
                user = user,
                isUserLiked = item.likedUsers?.any { it == user.id } ?: false,
                isUserSaved = user.savedPosts.any { it == item.id },
                onSaveClick = { onSaveClick.invoke(item.id) },
                onLikeClick = { onLikeClick.invoke(item.id, item.likedUsers.orEmpty()) })

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
