package com.example.jobsearchapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.example.jobsearchapp.Proposal
import com.example.jobsearchapp.notifications.ApplicationStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserApplicationsScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    // Since userProposals is a List<Proposal>, not a StateFlow, we don't need collectAsState
    val userProposals = viewModel.userProposals
    LaunchedEffect(Unit) {
        viewModel.loadUserProposals()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Applications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (userProposals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven't applied to any jobs yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userProposals) { proposal ->
                    UserProposalCard(proposal = proposal, viewModel = viewModel, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProposalCard(
    proposal: Proposal,
    viewModel: AppViewModel,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = proposal.jobTitle,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Status: ${proposal.status.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (proposal.status) {
                    ApplicationStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
                    ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Convert Timestamp to Date for display
            val date = proposal.timestamp?.toDate() ?: Date()
            Text(
                text = "Submitted: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add this to the ApplicantProposalCard function, inside the action buttons Row
            Button(
                onClick = {
                    viewModel.createChat(
                        proposal = proposal,
                        onSuccess = { chatId ->
                            navController.navigate("chat/$chatId")
                        },
                        onError = { /* Handle error */ }
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
                Text("Message")
            }
        }
    }
}
