package com.example.jobsearchapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.jobsearchapp.WorkExperience

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkExperienceScreen(
    workExperience: WorkExperience?,
    onBackClick: () -> Unit,
    onSaveClick: (WorkExperience) -> Unit,
    onDeleteClick: () -> Unit
) {
    // Create default values in case workExperience is null
    val defaultExperience = WorkExperience(
        id = 0,
        userId = 0,
        position = "",
        startDate = "",
        endDate = "",
        description = "",
        isCurrentJob = false,
        company = "",
    )

    // Use the workExperience if not null, otherwise use default values
    val experience = workExperience ?: defaultExperience

    // Now create mutable state variables with non-null values
    var company by remember { mutableStateOf(experience.company) }
    var position by remember { mutableStateOf(experience.position) }
    var startDate by remember { mutableStateOf(experience.startDate) }
    var endDate by remember { mutableStateOf(experience.endDate ?: "") }
    var description by remember { mutableStateOf(experience.description) }
    var isCurrentJob by remember { mutableStateOf(experience.isCurrentJob) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Work Experience") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = position,
                onValueChange = { position = it },
                label = { Text("Position") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start Date (MM/YYYY)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCurrentJob as Boolean,
                    onCheckedChange = { isCurrentJob = it }
                )
                Text("I currently work here")
            }

            if (isCurrentJob as Boolean) {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (MM/YYYY)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Button(
                onClick = {
                    val updatedWorkExperience = (if (isCurrentJob as Boolean) null else endDate)?.let {
                        experience.copy(
                            company = company,
                            position = position,
                            startDate = startDate,
                            endDate = it,
                            description = description,
                            isCurrentJob = isCurrentJob
                        )
                    }
                    if (updatedWorkExperience != null) {
                        onSaveClick(updatedWorkExperience)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
