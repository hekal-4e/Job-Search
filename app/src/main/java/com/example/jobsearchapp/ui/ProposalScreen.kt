package com.example.jobsearchapp.ui

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.example.jobsearchapp.Proposal
import com.example.jobsearchapp.R
import com.example.jobsearchapp.model.Job
import com.example.jobsearchapp.model.NotificationItem
import com.example.jobsearchapp.notifications.ApplicationStatus
import com.example.jobsearchapp.notifications.NotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import com.airbnb.lottie.compose.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun proposalscreen(
    navController: NavController,
    viewModel: AppViewModel,
    job: Job  // This is the parameter name
) {
    val context = LocalContext.current
    val notificationService = remember { NotificationService(context) }
    var cvUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }
    var cvFileName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var proposalText by remember { mutableStateOf("") }
    var cvLink by remember { mutableStateOf("") }
    var portfolioLink by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var showSuccessPage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cvPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            cvUri = it
            // Get file name from URI
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cvFileName = cursor.getString(nameIndex)
            }
        }
    }

    // Success animation
    val compositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.success1)
    )
    val progress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        isPlaying = showSuccessAnimation,
        iterations = 1,
        speed = 1.0f
    )

    // When animation completes, show success page
    LaunchedEffect(progress) {
        if (progress == 1f && showSuccessAnimation) {
            showSuccessPage = true
            showSuccessAnimation = false
        }
    }

    if (showSuccessPage) {
        ApplicationSuccessScreen(
            navController = navController,
            jobTitle = job.title,
            companyName = job.company
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Apply for Job") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Job title and company
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.company,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Phone number field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("Enter your phone number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                // CV link field
                OutlinedTextField(
                    value = cvLink,
                    onValueChange = { cvLink = it },
                    label = { Text("CV/Resume Link") },
                    placeholder = { Text("Paste link to your CV/Resume") },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Portfolio link field
                OutlinedTextField(
                    value = portfolioLink,
                    onValueChange = { portfolioLink = it },
                    label = { Text("Portfolio Link (Optional)") },
                    placeholder = { Text("Paste link to your portfolio") },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Proposal text field
                OutlinedTextField(
                    value = proposalText,
                    onValueChange = { proposalText = it },
                    label = { Text("Cover Letter") },
                    placeholder = { Text("Write your cover letter here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    maxLines = 10
                )
                Spacer(modifier = Modifier.height(24.dp))
                // CV Upload Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Upload Your CV",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                if (!isUploading && cvUri == null) {
                                    cvPicker.launch("application/pdf")
                                }
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isUploading -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = { uploadProgress },
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Uploading: ${(uploadProgress * 100).toInt()}%")
                                }
                            }
                            cvUri != null -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "CV Uploaded",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cvFileName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = "Upload CV",
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Click to upload your CV (PDF)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    if (cvUri != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    cvUri = null
                                    cvFileName = ""
                                }
                            ) {
                                Text("Remove")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Submit button
                Button(
                    onClick = {
                        if (proposalText.isBlank()) {
                            errorMessage = "Please write a cover letter"
                            return@Button
                        }
                        if (phoneNumber.isBlank()) {
                            errorMessage = "Please enter your phone number"
                            return@Button
                        }
                        if (cvFileName.isBlank()) {
                            errorMessage = "Please upload your c.v"
                            return@Button
                        }
                        // Clear error message
                        errorMessage = null
                        // Check if we need to upload CV first
                        if (cvUri != null) {
                            isUploading = true
                            uploadCvToFirebase(
                                cvUri = cvUri!!,
                                jobId = job.id,
                                onProgress = { progress ->
                                    uploadProgress = progress
                                },
                                onSuccess = { cvDownloadUrl ->
                                    isUploading = false
                                    // Now submit the application with the CV URL
                                    submitProposal(
                                        navController = navController,
                                        notificationService = notificationService,
                                        job = job,
                                        proposalText = proposalText,
                                        phoneNumber = phoneNumber,
                                        cvLink = cvDownloadUrl, // Use the uploaded file URL
                                        portfolioLink = portfolioLink,
                                        onSending = { isSending = it },
                                        onError = { errorMessage = it },
                                        onSuccess = {
                                            // Show animation instead of dialog
                                            showSuccessAnimation = true
                                        }
                                    )
                                },
                                onFailure = { exception ->
                                    isUploading = false
                                    errorMessage = "Failed to upload CV: ${exception.message}"
                                }
                            )
                        } else {
                            // Submit without uploading CV
                            submitProposal(
                                navController = navController,
                                notificationService = notificationService,
                                job = job,
                                proposalText = proposalText,
                                phoneNumber = phoneNumber,
                                cvLink = cvLink, // Use the link provided in the text field
                                portfolioLink = portfolioLink,
                                onSending = { isSending = it },
                                onError = { errorMessage = it },
                                onSuccess = {
                                    // Show animation instead of dialog
                                    showSuccessAnimation = true
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSending && !isUploading
                ) {
                    if (isSending || isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submitting...")
                    } else {
                        Text("Submit Application")
                    }
                }
                // Error message
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Show animation overlay when submitting
            if (showSuccessAnimation) {
                Dialog(
                    onDismissRequest = { },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = compositionResult.value,
                            progress = { progress },
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
        }
    }
}


// Function to upload CV to Firebase Storage
private fun uploadCvToFirebase(
    cvUri: Uri,
    jobId: String,
    onProgress: (Float) -> Unit,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
        onFailure(Exception("User not logged in"))
        return
    }
    val fileName = "${UUID.randomUUID()}_${currentUser.uid}_${jobId}.pdf"
    val storageRef = FirebaseStorage.getInstance().reference
        .child("cvs")
        .child(currentUser.uid)
        .child(fileName)
    storageRef.putFile(cvUri)
        .addOnProgressListener { taskSnapshot ->
            val progress = taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()
            onProgress(progress)
        }
        .addOnSuccessListener {
            // Get download URL
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}

// Function to submit the proposal
private fun submitProposal(
    navController: NavController,
    notificationService: NotificationService,
    job: Job,
    proposalText: String,
    phoneNumber: String,
    cvLink: String,
    portfolioLink: String,
    onSending: (Boolean) -> Unit,
    onError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    // Get current user
    val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
        onError("You must be logged in to apply")
        return
    }
    onSending(true)
    // Create proposal
    val proposal = Proposal(
        id = UUID.randomUUID().toString(),
        jobId = job.id,
        jobTitle = job.title,
        applicantId = currentUser.uid,
        applicantName = currentUser.displayName ?: "",
        applicantEmail = currentUser.email ?: "",
        applicantPhone = phoneNumber,
        coverLetter = proposalText,
        cvUrl = cvLink,
        portfolioUrl = portfolioLink,
        timestamp = com.google.firebase.Timestamp.now(),
        status = ApplicationStatus.SENT,
    )
    // Save to Firestore
    FirebaseFirestore.getInstance().collection("proposals")
        .document(proposal.id)
        .set(proposal)
        .addOnSuccessListener {
            // Show success dialog
            onSending(false)
            onSuccess()
            // Send notification to applicant
            notificationService.showJobNotification(proposal)
            // Create notification in Firestore
            val notification = NotificationItem(
                title = "Application Sent",
                message = "Your application for ${job.title} at ${job.company} has been submitted successfully.",
                userId = currentUser.uid,
                type = "proposal",
                relatedId = proposal.id
            )
            saveNotificationToFirestore(notification)
            // Also notify the job poster
            val recruiterNotification = NotificationItem(
                title = "New Application Received",
                message = "A new application for ${job.title} has been received from ${currentUser.displayName ?: currentUser.email}.",
                userId = job.uid,
                type = "new_application",
                relatedId = proposal.id
            )
            saveNotificationToFirestore(recruiterNotification)
        }
        .addOnFailureListener {
            onSending(false)
            onError("Failed to submit application: ${it.message}")
        }
}

private fun saveNotificationToFirestore(notification: NotificationItem) {
    val db = FirebaseFirestore.getInstance()
    // Create a notification with a proper Timestamp
    val notificationData = hashMapOf(
        "userId" to notification.userId,
        "title" to notification.title,
        "message" to notification.message,
        "timestamp" to com.google.firebase.Timestamp.now(),
        "read" to false,
        "type" to notification.type,
        "relatedId" to notification.relatedId
    )
    db.collection("notifications")
        .add(notificationData)
        .addOnSuccessListener {
            // Notification saved successfully
        }
        .addOnFailureListener {
            // Failed to save notification
        }
}
