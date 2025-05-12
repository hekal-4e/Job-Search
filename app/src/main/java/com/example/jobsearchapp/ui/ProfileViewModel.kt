package com.example.jobsearchapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri.asStateFlow()

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                _profileImageUri.value = uri
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun checkAndRequestPermission(
        context: Context,
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        onPermissionGranted: () -> Unit
    ) {
        when {
            // For Android 13 and above
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        onPermissionGranted()
                    }
                    else -> permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
            // For Android 12 and below
            else -> {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        onPermissionGranted()
                    }
                    else -> permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
}
