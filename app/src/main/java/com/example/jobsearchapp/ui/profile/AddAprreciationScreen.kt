package com.example.jobsearchapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppreciationScreen(
    onBackClick: () -> Unit,
    onSaveClick: (String) -> Unit
) {
    var appreciationText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Appreciation") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = appreciationText,
                onValueChange = {
                    appreciationText = it
                    isError = it.isBlank()
                },
                label = { Text("Appreciation") },
                modifier = Modifier.fillMaxWidth(),
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text("Appreciation cannot be empty")
                    }
                }
            )

            Button(
                onClick = {
                    if (appreciationText.isNotBlank()) {
                        onSaveClick(appreciationText)
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = appreciationText.isNotBlank()
            ) {
                Text("Save")
            }
        }
    }
}
