package com.example.lastday.screens

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lastday.data.ComplaintModel
import com.example.lastday.data.UserModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class MainViewModel : ViewModel() {
    private val authentication = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    var showLoadingDialog by mutableStateOf(false)
    var statusMessage: String? by mutableStateOf(null)

    var profileScreenState: ProfileScreenState by mutableStateOf(ProfileScreenState.Loading)
    var homeScreenState: HomeScreenState by mutableStateOf(HomeScreenState.Loading)
    var savedScreenState: SavedScreenState by mutableStateOf(SavedScreenState.Loading)

    private var users: List<UserModel>? = null
    private var complaints: List<ComplaintModel>? = null
    var numberOfComplaints: String by mutableStateOf("-")
    private var currentUser: UserModel? by mutableStateOf(null)
    var navigateBackFromCreate by mutableStateOf(false)

    fun signIn(
        email: String?,
        password: String?,
        onComplete: () -> Unit,
    ) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            statusMessage = "Email and password cant be empty"
            return
        }

        showLoadingDialog = true
        try {
            authentication.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete.invoke()
                } else {
                    statusMessage = "Error while sign in"
                }
                showLoadingDialog = false
            }
        } catch (e: Exception) {
            statusMessage = "An unexpected error was encountered"
            showLoadingDialog = false
        }

    }

    fun signOut() {
        authentication.signOut()
    }

    fun registerUser(
        email: String,
        password: String,
        onComplete: () -> Unit
    ) {
        showLoadingDialog = true
        try {
            authentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = authentication.currentUser
                    user?.let {
                        saveUserDetails(it.uid, email, onComplete)
                    }
                } else {
                    statusMessage = "Error while registration"
                    showLoadingDialog = false
                }
            }
        } catch (e: Exception) {
            statusMessage = "An unexpected error was encountered"
            showLoadingDialog = false
        }
    }

    fun getUserForProfile() {
        profileScreenState = ProfileScreenState.Loading
        getUser(
            onSuccess = {
                getUserComplaints()
                showLoadingDialog = false

            }, onError = {
                profileScreenState = ProfileScreenState.Error
                showLoadingDialog = false
            }
        )
    }

    fun getUserForHomeScreen() {
        homeScreenState = HomeScreenState.Loading
        getUser(
            onSuccess = {
                getAllUsers(onSuccess = {
                    getComplaints()
                }, onError = {
                    homeScreenState = HomeScreenState.Error
                })
            }, onError = {
                homeScreenState = HomeScreenState.Error
                showLoadingDialog = false
            }
        )
    }

    fun getUserForSavedScreen() {
        savedScreenState = SavedScreenState.Loading
        getUser(
            onSuccess = {
                getAllUsers(onSuccess = {
                    getSavedComplaints()
                }, onError = {
                    savedScreenState = SavedScreenState.Error
                })
            }, onError = {
                savedScreenState = SavedScreenState.Error
            }
        )
    }

    fun updateUser(
        name: String?,
        surname: String?,
        username: String?,
        about: String?,
        profileImageUrl: String? = null,
        profileImage: Uri?,
    ) {
        if (profileImage != null) {
            updateProfilePicture(profileImage) { url ->
                updateUser(name, surname, username, about, url, null)
            }
        }

        val user = mapOf(
            "name" to name,
            "surname" to surname,
            "username" to username,
            "about" to about,
            "profilePicture" to profileImageUrl
        )
        try {
            firestoreInstance.collection("user").document(authentication.uid.toString())
                .update(user)
                .addOnSuccessListener {
                    getUserForProfile()
                }
                .addOnFailureListener {
                    statusMessage = "Error while saving update user"
                }
        } catch (e: Exception) {
            statusMessage = "An unexpected error was encountered"
        }
    }

    fun createComplaint(message: String, picture: Uri?) {
        if (picture == null || message.isEmpty()) {
            statusMessage = "Please select an image and write a description"
            return
        }
        showLoadingDialog = true
        try {
            val data = mapOf(
                "message" to message,
                "userId" to authentication.currentUser?.uid,
                "dateTime" to Timestamp.now()
            )
            firestoreInstance.collection("complaints").add(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        uploadComplaintImage(imageName = it.result.id, picture = picture)
                    } else {
                        profileScreenState = ProfileScreenState.Error
                        showLoadingDialog = false
                    }
                }
        } catch (e: Exception) {
            statusMessage = "An unexpected error was encountered"
            profileScreenState = ProfileScreenState.Error
        }
    }

    fun updateSaveStatusForHome(complaintId: String) {
        updateSaveStatus(complaintId = complaintId, onSuccess = {
            homeScreenState = HomeScreenState.Content(
                users = users,
                complaints = complaints,
                savedPosts = currentUser?.savedPosts.orEmpty()
            )
        })
    }

    fun updateSaveStatusForSavedScreen(complaintId: String) {
        updateSaveStatus(complaintId = complaintId, onSuccess = {
            getSavedComplaints()
        })
    }

    fun updateSaveStatusForProfileScreen(complaintId: String) {
        updateSaveStatus(complaintId = complaintId, onSuccess = {
            getUserComplaints()
        })
    }

    fun updateLikeStatusForProfile(complaintId: String, likedUsers: List<String>) {
        updateLikeStatus(complaintId = complaintId, likedUsers = likedUsers, onSuccess = {
            getUserComplaints()
        })
    }

    fun updateLikeStatusForHome(complaintId: String, likedUsers: List<String>) {
        updateLikeStatus(complaintId = complaintId, likedUsers = likedUsers, onSuccess = {
            getComplaints()
        })
    }

    fun updateLikeStatusForSavedScreen(complaintId: String, likedUsers: List<String>) {
        updateLikeStatus(complaintId = complaintId, likedUsers = likedUsers, onSuccess = {
            getSavedComplaints()
        })
    }

    private fun getUser(onSuccess: (UserModel) -> Unit = {}, onError: () -> Unit = {}) {
        try {
            firestoreInstance.collection("user").document(authentication.uid.orEmpty()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.toObject(UserModel::class.java)?.let { user ->
                            currentUser = user.apply {
                                id = it.result.id
                            }
                            onSuccess.invoke(user)
                        }
                    } else {
                        onError.invoke()
                    }
                }
        } catch (e: Exception) {
            throwError()
            onError.invoke()
        }
    }

    private fun getUserComplaints() {
        try {
            firestoreInstance.collection("complaints").whereEqualTo("userId", authentication.uid.orEmpty()).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        complaints = it.result.toObjects(ComplaintModel::class.java)
                            .onEachIndexed { index, complaint ->
                                complaint.id = it.result.documents.get(index).id
                            }
                        numberOfComplaints = "${complaints?.size}"
                        currentUser?.let { user ->
                            profileScreenState = ProfileScreenState.Content(user, complaints)
                        }
                    } else {
                        profileScreenState = ProfileScreenState.Error
                    }
                }
        } catch (e: Exception) {
            throwError()
            profileScreenState = ProfileScreenState.Error
        }
    }

    private fun saveUserDetails(
        userId: String,
        email: String,
        onComplete: () -> Unit
    ) {
        val user = hashMapOf(
            "email" to email,
        )
        try {
            firestoreInstance.collection("user").document(userId)
                .set(user)
                .addOnSuccessListener {
                    onComplete.invoke()
                    throwError("User registered successfully!")
                }
                .addOnFailureListener {
                    throwError("Error while saving user info")
                }
        } catch (e: Exception) {
            throwError()
        }
    }

    private fun updateProfilePicture(picture: Uri, onComplete: (String) -> Unit) {
        showLoadingDialog = true
        try {
            val storageRef = firebaseStorage.getReference("profilePictures/" + authentication.uid)
            storageRef.putFile(picture).addOnCompleteListener {
                if (it.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener {
                        onComplete(it.toString())
                    }
                } else {
                    throwError("Error while updating profile picture")
                }
            }
        } catch (e: Exception) {
            throwError()
        }
    }

    private fun uploadComplaintImage(imageName: String, picture: Uri) {
        try {
            val storageRef = firebaseStorage.getReference("complaints/$imageName")
            storageRef.putFile(picture).addOnCompleteListener {
                if (it.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener {
                        updateComplaintImageUrl(complaintId = imageName, url = it.toString())
                    }
                } else {
                    throwError("Error while uploading complaint image")
                }
            }
        } catch (e: Exception) {
            throwError()
        }
    }

    private fun updateComplaintImageUrl(complaintId: String, url: String) {
        try {
            val data = mapOf("imageUrl" to url)
            firestoreInstance.collection("complaints").document(complaintId).update(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        navigateBackFromCreate = true
                        statusMessage = "Complaint created!"
                    } else {
                        profileScreenState = ProfileScreenState.Error
                    }
                    showLoadingDialog = false
                }
        } catch (e: Exception) {
            throwError()
            profileScreenState = ProfileScreenState.Error
        }
    }

    private fun getAllUsers(onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        try {
            firestoreInstance.collection("user").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        users = it.result.toObjects(UserModel::class.java)
                            .onEachIndexed { index, user ->
                                user.id = it.result.documents.get(index).id
                            }
                        onSuccess.invoke()
                    } else {
                        onError.invoke()
                    }
                }
        } catch (e: Exception) {
            throwError()
            onError.invoke()
        }
    }

    private fun getComplaints() {
        try {
            firestoreInstance.collection("complaints")
                .orderBy("dateTime", Query.Direction.DESCENDING).get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        complaints = it.result.toObjects(ComplaintModel::class.java)
                            .onEachIndexed { index, complaint ->
                                complaint.id = it.result.documents.get(index).id
                            }
                        homeScreenState = HomeScreenState.Content(
                            users = users,
                            complaints = complaints,
                            savedPosts = currentUser?.savedPosts.orEmpty()
                        )
                    } else {
                        homeScreenState = HomeScreenState.Error
                    }
                    showLoadingDialog = false
                }
        } catch (e: Exception) {
            throwError()
            homeScreenState = HomeScreenState.Error
        }
    }

    private fun updateSaveStatus(complaintId: String, onSuccess: () -> Unit = {}) {
        showLoadingDialog = true
        val list: ArrayList<String> = arrayListOf()
        currentUser?.savedPosts?.let(list::addAll)
        if (list.indexOf(complaintId) != -1) {
            list.remove(complaintId)
        } else {
            list.add(complaintId)
        }

        val user = mapOf(
            "savedPosts" to list,
        )
        try {
            firestoreInstance.collection("user").document(authentication.uid.toString())
                .update(user)
                .addOnSuccessListener {
                    getUser(onSuccess = {
                        onSuccess.invoke()
                    })
                    showLoadingDialog = false
                }
                .addOnFailureListener {
                    throwError("Error while saving post")
                }
        } catch (e: Exception) {
            throwError()
        }
    }

    private fun updateLikeStatus(
        complaintId: String,
        likedUsers: List<String>,
        onSuccess: () -> Unit = {}
    ) {
        showLoadingDialog = true
        val list: ArrayList<String> = arrayListOf()
        list.addAll(likedUsers)
        if (list.indexOf(currentUser?.id) != -1) {
            list.remove(currentUser?.id)
        } else {
            list.add(currentUser?.id.orEmpty())
        }

        val complaint = mapOf(
            "likedUsers" to list,
        )
        try {
            firestoreInstance.collection("complaints").document(complaintId)
                .update(complaint)
                .addOnSuccessListener {
                    onSuccess.invoke()
                    showLoadingDialog = false
                }
                .addOnFailureListener {
                    throwError("Error while updating like status")
                }
        } catch (e: Exception) {
            throwError()
        }
    }

    private fun getSavedComplaints() {
        if (currentUser?.savedPosts.isNullOrEmpty()) {
            savedScreenState = SavedScreenState.Content(users = users, complaints = complaints)
            return
        }
        try {
            firestoreInstance.collection("complaints")
                .whereIn(FieldPath.documentId(), currentUser?.savedPosts.orEmpty())
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        complaints = it.result.toObjects(ComplaintModel::class.java)
                            .onEachIndexed { index, complaint ->
                                complaint.id = it.result.documents.get(index).id
                            }

                        savedScreenState =
                            SavedScreenState.Content(users = users, complaints = complaints)
                    } else {
                        savedScreenState = SavedScreenState.Error
                    }
                }
        } catch (e: Exception) {
            throwError()
        }
    }

    private fun throwError(errorMessage: String = "An unexpected error was encountered") {
        statusMessage = errorMessage
        savedScreenState = SavedScreenState.Error
    }
}

sealed class ProfileScreenState {
    object Loading : ProfileScreenState()
    object Error : ProfileScreenState()
    class Content(
        val user: UserModel,
        val complaints: List<ComplaintModel>?
    ) : ProfileScreenState()
}

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    object Error : HomeScreenState()
    class Content(
        val users: List<UserModel>?,
        val complaints: List<ComplaintModel>?,
        val savedPosts: List<String>,
        val currentUserId: String = FirebaseAuth.getInstance().uid.orEmpty()
    ) :
        HomeScreenState()
}

sealed class SavedScreenState {
    object Loading : SavedScreenState()
    object Error : SavedScreenState()
    class Content(
        val users: List<UserModel>?,
        val complaints: List<ComplaintModel>?,
        val currentUserId: String = FirebaseAuth.getInstance().uid.orEmpty()
    ) : SavedScreenState()
}
