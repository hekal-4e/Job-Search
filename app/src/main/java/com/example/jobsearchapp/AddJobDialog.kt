package com.example.jobsearchapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.model.JobCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJobDialog(
    navController: NavController,
    viewModel: AppViewModel,
    onDismissRequest: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    // Job type dropdown
    var jobType by remember { mutableStateOf("") }
    var showJobTypeDropdown by remember { mutableStateOf(false) }
    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Internship", "Freelance", "Remote")

    // Category dropdown
    var selectedCategory by remember { mutableStateOf(JobCategory.TECHNICAL) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var categorySearchQuery by remember { mutableStateOf("") }

    // Filter categories based on search query
    val filteredCategories = remember(categorySearchQuery) {
        if (categorySearchQuery.isEmpty()) {
            JobCategory.values().toList()
        } else {
            JobCategory.values().filter {
                it.displayName.contains(categorySearchQuery, ignoreCase = true)
            }
        }
    }

    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post a Job") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = { },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showCategoryDropdown = true
                            categorySearchQuery = ""
                        }) {
                            Icon(Icons.Default.ArrowDropDown, "Show categories")
                        }
                    }
                )

                DropdownMenu(
                    expanded = showCategoryDropdown,
                    onDismissRequest = { showCategoryDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .heightIn(max = 350.dp)
                ) {
                    OutlinedTextField(
                        value = categorySearchQuery,
                        onValueChange = { categorySearchQuery = it },
                        placeholder = { Text("Search categories") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        singleLine = true
                    )

                    Divider()

                    // List of categories
                    if (filteredCategories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No matching categories found")
                        }
                    } else {
                        filteredCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = jobType,
                    onValueChange = { },
                    label = { Text("Job Type") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showJobTypeDropdown = true }) {
                            Icon(Icons.Default.ArrowDropDown, "Show job types")
                        }
                    }
                )

                DropdownMenu(
                    expanded = showJobTypeDropdown,
                    onDismissRequest = { showJobTypeDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    jobTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                jobType = type
                                showJobTypeDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salary Range (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Job Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text("Requirements") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Button(
                onClick = {
                    if (title.isNotBlank() && company.isNotBlank() && location.isNotBlank() &&
                        description.isNotBlank() && requirements.isNotBlank() && jobType.isNotBlank()) {
                        isSubmitting = true
                        scope.launch {
                            try {
                                viewModel.addJob(
                                    title = title,
                                    company = company,
                                    location = location,
                                    description = description,
                                    requirements = requirements,
                                    salary = salary,
                                    type = jobType,
                                    category = selectedCategory.name
                                )
                                navController.popBackStack()
                            } catch (e: Exception) {
                                // Handle error
                                isSubmitting = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && company.isNotBlank() && location.isNotBlank() &&
                        description.isNotBlank() && requirements.isNotBlank() && jobType.isNotBlank() &&
                        !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Post Job")
                }
            }
        }
    }
}
