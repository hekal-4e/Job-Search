package com.example.jobsearchapp.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jobsearchapp.model.Job

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    job: Job,
    selectedCvUri: Uri?,
    proposalText: String,
    onProposalTextChange: (String) -> Unit,
    onCvPick: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply for ${job.title}") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = proposalText,
                    onValueChange = onProposalTextChange,
                    label = { Text("Your Proposal") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCvPick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedCvUri != null) "CV Selected" else "Upload CV (PDF)")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSubmit) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
