package com.example.jobsearchapp.ui.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.jobsearchapp.UserProfile
import android.widget.Toast
import com.example.jobsearchapp.util.ImageUploadHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    navController: NavHostController,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileImageuploaded: (String) -> Unit,
    onBack: () -> Unit = { navController.popBackStack() },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isUploading by ImageUploadHelper.isLoading.collectAsState()
    val uploadError by ImageUploadHelper.error.collectAsState()

    // State for share bottom sheet
    var showShareSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // State for permission dialog
    var showPermissionDialog by remember { mutableStateOf(false) }

    // File picker launcher - declare this BEFORE using it
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            ImageUploadHelper.uploadProfileImage(it) { downloadUrl ->
                // Call the callback when upload is successful
                onProfileImageuploaded(downloadUrl)
                Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with file access
            fileLauncher.launch("image/*")
        } else {
            // Permission denied, show a message or handle accordingly
            showPermissionDialog = true
        }
    }

    // Function to check and request storage permission
    fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)
    }

    // Generate profile share link
    val profileShareLink = "https://yourapp.com/profile/${userProfile.id}"

    // Function to handle sharing to specific apps
    fun shareToApp(packageName: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out my professional profile: $profileShareLink")
            `package` = packageName
        }
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            // Handle case where app is not installed
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Check out my professional profile: $profileShareLink")
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Share profile via"))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(onClick = { showShareSheet = true }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = {
                        try {
                            onSettingsClick()
                        } catch (e: Exception) {
                            // Fallback if onSettingsClick has an error
                            try {
                                navController.navigate("settings")
                            } catch (e2: Exception) {
                                // Prevent crash
                                Toast.makeText(context, "Settings not available", Toast.LENGTH_SHORT).show()
                                Log.e("ProfileScreen", "Navigation error: ${e2.message}")
                            }
                        }
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 0.dp,
                bottom = paddingValues.calculateBottomPadding(),
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header with Profile Picture Change functionality
            item {
                Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
                    ProfileHeaderWithPictureChange(
                        userProfile = userProfile,
                        onEditClick = {
                            try {
                                onEditClick()
                            } catch (e: Exception) {
                                // Prevent crash by catching the exception
                                Log.e("ProfileScreen", "Edit profile error: ${e.message}")
                                Toast.makeText(
                                    context,
                                    "Edit profile not available yet",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Try to navigate directly if the callback fails
                                try {
                                    navController.navigate("edit_profile")
                                } catch (e2: Exception) {
                                    Log.e("ProfileScreen", "Navigation error: ${e2.message}")
                                }
                            }
                        },
                        onChangeProfilePicture = { checkAndRequestPermission() }
                    )
                }
            }

            // About Me Section
            item {
                AboutMeSection(
                    aboutMe = userProfile.aboutMe ?: "",
                    onEditClick = {
                        try {
                            navController.navigate("edit_about_me")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Work Experience Section
            item {
                // Convert UserProfile work experience to UI model
                val workExperiences = userProfile.workExperience?.map {
                    com.example.jobsearchapp.WorkExperience(
                        id = it.id,
                        company = it.company,
                        startDate = it.startDate,
                        endDate = it.endDate,
                        description = it.description
                    )
                } ?: emptyList()

                WorkExperienceSection(
                    workExperiences = workExperiences,
                    onEditClick = { experienceId ->
                        try {
                            navController.navigate("edit_work_experience/$experienceId")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAddClick = {
                        try {
                            navController.navigate("add_work_experience")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Education Section
            item {
                // Convert UserProfile education to UI model
                val educations = userProfile.education?.map {
                    Education(
                        id = it.id.toString(),
                        degree = it.degree,
                        institution = it.institution,
                        startDate = it.startDate,
                        endDate = it.endDate,
                        description = it.description
                    )
                } ?: emptyList()

                EducationSection(
                    educations = educations,
                    onEditClick = { educationId ->
                        try {
                            navController.navigate("edit_education/$educationId")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onAddClick = {
                        try {
                            navController.navigate("add_education")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Skills Section
            item {
                val skills = userProfile.skills?.map { it.skillName } ?: emptyList()

                SkillsSection(
                    skills = skills,
                    onEditClick = {
                        try {
                            navController.navigate("edit_skills")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Languages Section
            item {
                val languages = userProfile.languages?.map { it.languageName } ?: emptyList()

                LanguagesSection(
                    languages = languages,
                    onAddClick = {
                        try {
                            navController.navigate("add_language")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onEditClick = { languageName ->
                        try {
                            userProfile.languages?.firstOrNull { it.languageName == languageName }?.id?.let { id ->
                                navController.navigate("edit_language/$id")
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Appreciations Section
            // Appreciations Section
            item {
                // Create a list of UI Appreciations with dummy data if needed
                val appreciations = if (userProfile.appreciations != null && userProfile.appreciations.isNotEmpty()) {
                    // Create dummy appreciations based on the count of items in the original list
                    userProfile.appreciations.mapIndexed { index, _ ->
                        Appreciation(
                            id = index.toString(),
                            title = "Appreciation ${index + 1}",
                            fromPerson = "Colleague",
                            description = "This is a placeholder for appreciation ${index + 1}"
                        )
                    }
                } else {
                    emptyList()
                }

                AppreciationSection(
                    appreciations = appreciations,
                    onEditClick = {
                        try {
                            navController.navigate("edit_appreciations")
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Navigation error: ${e.message}")
                            Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

        }

        // Share Bottom Sheet
        if (showShareSheet) {
            ModalBottomSheet(
                onDismissRequest = { showShareSheet = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Share Profile",
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = { showShareSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Profile link with copy button
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = profileShareLink,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(profileShareLink))
                                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Share via",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Social media sharing options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SocialMediaButton(
                            name = "WhatsApp",
                            onClick = { shareToApp("com.whatsapp") }
                        )
                        SocialMediaButton(
                            name = "Facebook",
                            onClick = { shareToApp("com.facebook.katana") }
                        )
                        SocialMediaButton(
                            name = "Instagram",
                            onClick = { shareToApp("com.instagram.android") }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SocialMediaButton(
                            name = "LinkedIn",
                            onClick = { shareToApp("com.linkedin.android") }
                        )
                        SocialMediaButton(
                            name = "Twitter",
                            onClick = { shareToApp("com.twitter.android") }
                        )
                        SocialMediaButton(
                            name = "Messenger",
                            onClick = { shareToApp("com.facebook.orca") }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    // General share button
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out my professional profile: $profileShareLink")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share profile via"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Share with other apps")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Permission Dialog
        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                title = { Text("Permission Required") },
                text = {
                    Text("To upload files, we need permission to access your device storage. Please grant this permission to continue.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPermissionDialog = false
                            checkAndRequestPermission()
                        }
                    ) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPermissionDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SocialMediaButton(
    name: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.width(80.dp)
        ) {
            Text(name.take(2))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ProfileHeaderWithPictureChange(
    userProfile: UserProfile,
    onEditClick: () -> Unit,
    onChangeProfilePicture: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image with change button
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onChangeProfilePicture() },
            contentAlignment = Alignment.Center
        ) {
            if (userProfile.profileImageUrl != null) {
                // Display existing profile image from URL
                AsyncImage(
                    model = userProfile.profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Display placeholder
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Small camera icon or edit indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change Profile Picture",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = "${userProfile.firstName ?: ""} ${userProfile.lastName ?: ""}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Job Title
        Text(
            text = userProfile.jobTitle ?: "No job title",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Location
        Text(
            text = userProfile.location ?: "No location",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Edit Basic Info")
        }
    }
}

@Composable
fun AboutMeSection(
    aboutMe: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "About Me",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit About Me")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = aboutMe.ifEmpty { "Add a description about yourself" },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun WorkExperienceSection(
    workExperiences: List<WorkExperience>,
    onEditClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Work Experience",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Add Work Experience")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (workExperiences.isEmpty()) {
                Text(
                    text = "Add your work experience",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                workExperiences.forEach { experience ->
                    WorkExperienceItem(
                        experience = experience,
                        onEditClick = { onEditClick((experience.id ?: "").toString()) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WorkExperienceItem(
    experience: WorkExperience,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = experience.jobTitle ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = experience.company ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${experience.startDate ?: ""} - ${experience.endDate ?: "Present"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = experience.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
    }
}

@Composable
fun EducationSection(
    educations: List<Education>,
    onEditClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Education",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Add Education")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (educations.isEmpty()) {
                Text(
                    text = "Add your education",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                educations.forEach { education ->
                    EducationItem(
                        education = education,
                        onEditClick = { onEditClick(education.id ?: "") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun EducationItem(
    education: Education,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = education.degree ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = education.institution ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${education.startDate ?: ""} - ${education.endDate ?: "Present"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = education.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
    }
}

@Composable
fun SkillsSection(
    skills: List<String>,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Skills",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Skills")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (skills.isEmpty()) {
                Text(
                    text = "Add your skills",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    skills.forEach { skill ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguagesSection(
    languages: List<String>,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Languages",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Add Language")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (languages.isEmpty()) {
                Text(
                    text = "Add languages you speak",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = { onEditClick(language) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun AppreciationSection(
    appreciations: List<Appreciation>,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Appreciations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Appreciations")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (appreciations.isEmpty()) {
                Text(
                    text = "Add appreciations you've received",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                appreciations.forEach { appreciation ->
                    AppreciationItem(appreciation = appreciation)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AppreciationItem(appreciation: Appreciation) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = appreciation.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "From: ${appreciation.fromPerson ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = appreciation.description ?: "",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

