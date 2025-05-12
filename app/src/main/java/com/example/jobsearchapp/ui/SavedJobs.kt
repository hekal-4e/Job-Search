package com.example.jobsearchapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.model.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(
    navController: NavController,
    savedJobs: List<Job>
) {
    // No need to use collectAsState() since savedJobs is already a List<Job>
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Jobs") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (savedJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No jobs saved yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(savedJobs) { job ->
                    SavedJobCard(
                        job = job,
                        onJobClick = {
                            navController.navigate("jobDetails/${job.id}")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobCard(
    job: Job,
    onJobClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onJobClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = job.company,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SurfaceTag(text = job.location)
                SurfaceTag(text = job.type)
                if (job.salary.isNotEmpty()) {
                    SurfaceTag(text = job.salary)
                }
            }
        }
    }
}

@Composable
fun SurfaceTag(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
