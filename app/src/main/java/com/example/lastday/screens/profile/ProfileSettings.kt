package com.example.lastday.screens.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.lastday.components.ErrorComponent
import com.example.lastday.components.LoadingComponent
import com.example.lastday.components.ProfileImage
import com.example.lastday.data.UserModel
import com.example.lastday.screens.MainViewModel
import com.example.lastday.screens.ProfileScreenState

@Composable
fun ProfileSettings(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getUserForProfile()
    }

    when (val state = viewModel.profileScreenState) {
        is ProfileScreenState.Content -> SettingsContent(user = state.user, viewModel = viewModel)

        ProfileScreenState.Error -> ErrorComponent {
            viewModel.getUserForProfile()
        }

        ProfileScreenState.Loading -> LoadingComponent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(user: UserModel, viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var username: String by remember { mutableStateOf(user.username.orEmpty()) }
        var name: String by remember { mutableStateOf(user.name.orEmpty()) }
        var surname: String by remember { mutableStateOf(user.surname.orEmpty()) }
        var about: String by remember { mutableStateOf(user.about.orEmpty()) }

        var imageUri: Uri? by remember { mutableStateOf(null) }
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
        val context = LocalContext.current

        val launcher = rememberLauncherForActivityResult(
            contract =
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }


        LaunchedEffect(key1 = imageUri) {
            if (imageUri == null) return@LaunchedEffect

            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, imageUri)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, imageUri!!)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
        }

        ProfileImage(
            imageBitmap = bitmap.value,
            url = user.profilePicture,
            size = DpSize(width = 250.dp, height = 250.dp),
            onClick = {
                launcher.launch("image/*")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Username") },
            value = username,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { username = it })

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            value = name,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { name = it })

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Surname") },
            value = surname,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { surname = it })

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "About") },
            value = about,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { about = it })

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.updateUser(
                    name = name,
                    surname = surname,
                    username = username,
                    about = about,
                    profileImage = imageUri
                )
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Update Profile")
        }

    }
}