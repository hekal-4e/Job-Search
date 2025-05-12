package com.example.jobsearchapp.util

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ImageUploadHelper(downloadUrl: String) {
    companion object {
        private const val TAG = "ImageUploadHelper"

        // Firebase instances
        private val auth = FirebaseAuth.getInstance()
        @SuppressLint("StaticFieldLeak")
        private val db = FirebaseFirestore.getInstance()
        private val storage = FirebaseStorage.getInstance()

        // State flows
        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error

        // Function to upload profile image
        fun uploadProfileImage(imageUri: Uri, onSuccess: (String) -> Unit) {
            try {
                _isLoading.value = true

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _error.value = "User not logged in"
                    _isLoading.value = false
                    return
                }

                val userId = currentUser.uid

                // Create a reference to the storage location
                val storageRef = storage.reference.child("profile_images/$userId.jpg")

                // Upload the file
                storageRef.putFile(imageUri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        storageRef.downloadUrl
                    }
                    .addOnCompleteListener { task ->
                        _isLoading.value = false

                        if (task.isSuccessful) {
                            val downloadUri = task.result.toString()

                            // Update Firestore with the new image URL
                            db.collection("profiles").document(userId)
                                .update("profileImageUrl", downloadUri)
                                .addOnSuccessListener {
                                    // Call the success callback with the download URL
                                    onSuccess(downloadUri)
                                }
                                .addOnFailureListener { e ->
                                    _error.value = "Failed to update profile image: ${e.message}"
                                }
                        } else {
                            _error.value = "Failed to upload image: ${task.exception?.message}"
                        }
                    }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error uploading image: ${e.message}"
                Log.e(TAG, "Error in uploadProfileImage", e)
            }
        }
    }
}
