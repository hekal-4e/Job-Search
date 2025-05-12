package com.example.jobsearchapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    val job = remember(jobId, viewModel.jobs) {
        viewModel.jobs.find { it.id == jobId }
    }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val isJobPoster = job?.uid == currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isJobPoster) {
                        IconButton(
                            onClick = {
                                if (viewModel.isJobSaved(jobId)) {
                                    viewModel.removeSavedJob(jobId)
                                } else {
                                    job?.let { viewModel.saveJob(it) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (viewModel.isJobSaved(jobId)) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = if (viewModel.isJobSaved(jobId)) "Unsave Job" else "Save Job"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (job == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Job not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)

                    .padding(16.dp)
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = job.company,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.location,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.type,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = job.salary,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Job Description",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = job.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isJobPoster) {
                    // Show recruiter options
                    AnimatedButton(
                        onClick = { navController.navigate("recruiterProposals/${job.id}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Applications")
                    }
                } else {
                    // Show applicant options
                    AnimatedButton(
                        onClick = { navController.navigate("proposal/${job.id}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Now")
                    }
                }
            }
        }
    }
}
