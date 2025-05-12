package com.example.jobsearchapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jobsearchapp.model.Job

@Composable
fun JobCard(
    job: Job,
    isSaved: Boolean,
    onClick: () -> Unit,
    onApply: (String) -> Unit,
    onSave: () -> Unit,
    onUnsave: () -> Unit
) {
    var showApplyDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = job.title, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = if (isSaved) "Unsave Job" else "Save Job"
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = job.company, style = MaterialTheme.typography.bodyMedium)
            Text(text = job.location, style = MaterialTheme.typography.bodyMedium)
            Text(text = job.salary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedButton(onClick = { showApplyDialog = true }) {
                Text("Apply")
            }
        }
    }

    AnimatedVisibility(
        visible = showApplyDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AlertDialog(
            onDismissRequest = { showApplyDialog = false },
            title = { Text("Ready to Apply?") },
            text = { Text("Are you sure you want to apply for '${job.title}'?") },
            confirmButton = {
                AnimatedButton(
                    onClick = {
                        showApplyDialog = false
                        onApply(job.id)
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                AnimatedButton(
                    onClick = { showApplyDialog = false },
                    isTextButton = true
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}