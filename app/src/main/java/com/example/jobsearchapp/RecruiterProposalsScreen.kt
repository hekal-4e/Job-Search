package com.example.jobsearchapp.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.example.jobsearchapp.Proposal
import com.example.jobsearchapp.model.NotificationItem
import com.example.jobsearchapp.notifications.ApplicationStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterProposalsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    val context = LocalContext.current
    val job = viewModel.jobs.find { it.id == jobId }
    val proposals = viewModel.getProposalsForJob(jobId)
    val isLoading = viewModel.isLoading.value
    val currentUser = FirebaseAuth.getInstance().currentUser
    val scope = rememberCoroutineScope()

    LaunchedEffect(jobId) {
        viewModel.loadProposals(jobId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applications for ${job?.title ?: "Job"}") },
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (proposals.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No applications yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When candidates apply for this job, their applications will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Proposals list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(proposals) { proposal ->
                        var processingProposalId by remember { mutableStateOf<String?>(null) }
                        var isProcessing by remember { mutableStateOf(false) }

                        RecruiterProposalCard(
                            proposal = proposal,
                            isProcessing = isProcessing,
                            processingProposalId = processingProposalId,
                            viewModel = viewModel,
                            navController = navController,
                            onAccept = {
                                // Only allow if not already accepted or rejected
                                if (proposal.status != ApplicationStatus.ACCEPTED &&
                                    proposal.status != ApplicationStatus.REJECTED) {
                                    isProcessing = true
                                    processingProposalId = proposal.id
                                    updateProposalStatus(
                                        proposal = proposal,
                                        newStatus = ApplicationStatus.ACCEPTED,
                                        onSuccess = {
                                            isProcessing = false
                                            processingProposalId = null
                                            // Create notification for applicant
                                            val notification = NotificationItem(
                                                userId = proposal.applicantId,
                                                title = "Application Accepted",
                                                message = "Your application for ${job?.title} has been accepted!",
                                                read = false,
                                                type = "application_status",
                                                relatedId = proposal.id,
                                                timestamp = Timestamp.now()
                                            )
                                            saveNotificationToFirestore(
                                                notification = notification,
                                                onSuccess = {
                                                    // Force refresh proposals
                                                    viewModel.loadProposals(jobId)
                                                    Toast.makeText(
                                                        context,
                                                        "Applicant has been notified of acceptance",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onFailure = {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to notify applicant",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        },
                                        onFailure = {
                                            isProcessing = false
                                            processingProposalId = null
                                            Toast.makeText(
                                                context,
                                                "Failed to update application status",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            },
                            onReject = {
                                // Only allow if not already accepted or rejected
                                if (proposal.status != ApplicationStatus.ACCEPTED &&
                                    proposal.status != ApplicationStatus.REJECTED) {
                                    isProcessing = true
                                    processingProposalId = proposal.id
                                    updateProposalStatus(
                                        proposal = proposal,
                                        newStatus = ApplicationStatus.REJECTED,
                                        onSuccess = {
                                            isProcessing = false
                                            processingProposalId = null
                                            // Create notification for applicant
                                            val notification = NotificationItem(
                                                userId = proposal.applicantId,
                                                title = "Application Status Update",
                                                message = "Your application for ${job?.title} was not selected at this time.",
                                                read = false,
                                                type = "application_status",
                                                relatedId = proposal.id,
                                                timestamp = Timestamp.now()
                                            )
                                            saveNotificationToFirestore(
                                                notification = notification,
                                                onSuccess = {
                                                    // Force refresh proposals
                                                    viewModel.loadProposals(jobId)
                                                    Toast.makeText(
                                                        context,
                                                        "Applicant has been notified of rejection",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onFailure = {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to notify applicant",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        },
                                        onFailure = {
                                            isProcessing = false
                                            processingProposalId = null
                                            Toast.makeText(
                                                context,
                                                "Failed to update application status",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            },
                            onInterview = {
                                // Allow interviewing regardless of current status
                                // except if already accepted or rejected
                                if (proposal.status != ApplicationStatus.ACCEPTED &&
                                    proposal.status != ApplicationStatus.REJECTED) {
                                    isProcessing = true
                                    processingProposalId = proposal.id
                                    updateProposalStatus(
                                        proposal = proposal,
                                        newStatus = ApplicationStatus.INTERVIEWING,
                                        onSuccess = {
                                            isProcessing = false
                                            processingProposalId = null
                                            // Create notification for applicant
                                            val notification = NotificationItem(
                                                userId = proposal.applicantId,
                                                title = "Interview Request",
                                                message = "You've been selected for an interview for ${job?.title}. Check your messages for details.",
                                                read = false,
                                                type = "interview_request",
                                                relatedId = proposal.id,
                                                timestamp = Timestamp.now()
                                            )
                                            saveNotificationToFirestore(
                                                notification = notification,
                                                onSuccess = {
                                                    // Force refresh proposals
                                                    viewModel.loadProposals(jobId)
                                                    Toast.makeText(
                                                        context,
                                                        "Applicant has been notified of interview request",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    // Create chat and navigate to it
                                                    scope.launch {
                                                        viewModel.createChat(
                                                            proposal = proposal,
                                                            onSuccess = { chatId ->
                                                                navController.navigate("chat/$chatId")
                                                            },
                                                            onError = {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Failed to create chat",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        )
                                                    }
                                                },
                                                onFailure = {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to notify applicant",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        },
                                        onFailure = {
                                            isProcessing = false
                                            processingProposalId = null
                                            Toast.makeText(
                                                context,
                                                "Failed to update application status",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterProposalCard(
    proposal: Proposal,
    isProcessing: Boolean,
    processingProposalId: String?,
    viewModel: AppViewModel,
    navController: NavController,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onInterview: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Applicant info with call button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = proposal.applicantName.ifEmpty { proposal.applicantEmail },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = proposal.applicantEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    // Show phone number if available
                    if (proposal.applicantPhone.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = proposal.applicantPhone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                // Call button if phone number is available
                if (proposal.applicantPhone.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${proposal.applicantPhone}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Could not open phone app",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call applicant",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Proposal text preview
            Text(
                text = proposal.coverLetter,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Date submitted
            val date = proposal.timestamp?.toDate() ?: Date()
            Text(
                text = "Submitted: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            // Links row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CV link if available
                if (proposal.cvUrl.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(proposal.cvUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Invalid CV URL",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "View CV")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View CV")
                    }
                }
                // Portfolio link if available
                if (proposal.portfolioUrl.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(proposal.portfolioUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Invalid portfolio URL",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = "View Portfolio")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Portfolio")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Status and action buttons
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status badge with appropriate color
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (proposal.status) {
                        ApplicationStatus.ACCEPTED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        ApplicationStatus.INTERVIEWING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Status: ${proposal.status.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (proposal.status) {
                            ApplicationStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
                            ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.error
                            ApplicationStatus.INTERVIEWING -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chat button
                Button(
                    onClick = {
                        viewModel.createChat(
                            proposal = proposal,
                            onSuccess = { chatId ->
                                navController.navigate("chat/$chatId")
                            },
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Failed to create chat",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = "Chat"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Message Applicant")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Show action buttons based on current status
                // Only hide buttons if application is ACCEPTED or REJECTED
                if (proposal.status != ApplicationStatus.ACCEPTED &&
                    proposal.status != ApplicationStatus.REJECTED) {

                    // Disable all buttons when one is being processed
                    val buttonsEnabled = !isProcessing || processingProposalId != proposal.id

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAccept,
                            enabled = buttonsEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isProcessing && proposal.id == processingProposalId) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Accept"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Accept")
                            }
                        }

                        Button(
                            onClick = onInterview,
                            enabled = buttonsEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isProcessing && proposal.id == processingProposalId) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Interview"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Interview")
                            }
                        }

                        OutlinedButton(
                            onClick = onReject,
                            enabled = buttonsEnabled,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                                disabledContentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isProcessing && proposal.id == processingProposalId) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Reject"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reject")
                            }
                        }
                    }
                } else {
                    // Show a message about the current status
                    val statusMessage = when (proposal.status) {
                        ApplicationStatus.ACCEPTED -> "You've accepted this application"
                        ApplicationStatus.REJECTED -> "You've rejected this application"
                        else -> ""
                    }

                    if (statusMessage.isNotEmpty()) {
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to update proposal status in Firestore
private fun updateProposalStatus(
    proposal: Proposal,
    newStatus: ApplicationStatus,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("proposals").document(proposal.id)
        .update("status", newStatus.name)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener {
            onFailure()
        }
}

// Helper function to save notification to Firestore
private fun saveNotificationToFirestore(
    notification: NotificationItem,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val notificationData = hashMapOf(
        "userId" to notification.userId,
        "title" to notification.title,
        "message" to notification.message,
        "read" to notification.read,
        "type" to notification.type,
        "relatedId" to notification.relatedId,
        "timestamp" to notification.timestamp
    )

    db.collection("notifications")
        .add(notificationData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener {
            onFailure()
        }
}

