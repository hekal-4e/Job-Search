package com.example.jobsearchapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppreciationScreen(
    appreciations: List<String>,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    onRemoveClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appreciations") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Appreciation")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (appreciations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No appreciations added yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(appreciations) { appreciation ->
                    AppreciationItem(
                        appreciation = appreciation,
                        onRemoveClick = { onRemoveClick(appreciation) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppreciationItem(
    appreciation: String,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appreciation,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}
