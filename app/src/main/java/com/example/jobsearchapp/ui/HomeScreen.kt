package com.example.jobsearchapp.ui

import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.example.jobsearchapp.R
import com.example.jobsearchapp.model.Job
import com.example.jobsearchapp.model.JobCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AppViewModel,
    onThemeChange: () -> Unit,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedJobType by remember { mutableStateOf("All") }

    // Dropdown menu states
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showJobTypeDropdown by remember { mutableStateOf(false) }

    // Get categories from JobCategory enum for dropdown
    val categories = remember {
        listOf("All") + JobCategory.entries.map { it.displayName }.sorted()
    }

    // Job type filters
    val jobTypes = listOf("All", "Full-time", "Part-time", "Contract", "Internship", "Freelance", "Remote")

    val focusManager = LocalFocusManager.current

    // Filter jobs based on search query, category, and job type
    val filteredJobs = remember(viewModel.jobs, searchQuery, selectedCategory, selectedJobType) {
        viewModel.jobs.filter { job ->
            // Filter by search query
            val matchesSearch = job.title.contains(searchQuery, ignoreCase = true) ||
                    job.company.contains(searchQuery, ignoreCase = true) ||
                    job.description.contains(searchQuery, ignoreCase = true)

            // Filter by category
            val matchesCategory = if (selectedCategory == "All") {
                true
            } else {
                try {
                    // Find the enum value that matches the selected display name
                    val categoryEnum = JobCategory.entries.find { it.displayName == selectedCategory }
                    job.category == categoryEnum?.name
                } catch (e: Exception) {
                    false
                }
            }

            // Filter by job type
            val matchesJobType = selectedJobType == "All" || job.type == selectedJobType

            matchesSearch && matchesCategory && matchesJobType
        }
    }

    // Get unread messages count
    val unreadMessagesCount by remember { mutableIntStateOf(0) } // Replace with actual count from viewModel

    LaunchedEffect(Unit) {
        viewModel.loadJobs()
        viewModel.loadNotifications()
        // Add code to load unread messages count
    }

    // Rest of the HomeScreen implementation remains the same...

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("J0bVerse") },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate("chats")
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadMessagesCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (unreadMessagesCount > 99) "99+"
                                            else unreadMessagesCount.toString()
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Messages"
                            )
                        }
                    }
                    // Notification icon
                    IconButton(
                        onClick = {
                            Log.d("Navigation", "Notification icon clicked")
                            navController.navigate("notifications")
                            Log.d("Navigation", "Navigation attempted")
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (viewModel.unreadNotificationsCount > 0) {
                                    Badge {
                                        Text(
                                            text = if (viewModel.unreadNotificationsCount > 99) "99+"
                                            else viewModel.unreadNotificationsCount.toString()
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                    IconButton(onClick = onThemeChange) {
                        Icon(Icons.Default.ColorLens, contentDescription = "Change Theme")
                    }
                    IconButton(
                            onClick = {
                                onLogout()
                            }

                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search jobs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            // Filter section with dropdowns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category filter dropdown
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            IconButton(onClick = { showCategoryDropdown = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.heightIn(max = 350.dp)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedJobType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Job Type") },
                        trailingIcon = {
                            IconButton(onClick = { showJobTypeDropdown = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Job Type")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = showJobTypeDropdown,
                        onDismissRequest = { showJobTypeDropdown = false }
                    ) {
                        jobTypes.forEach { jobType ->
                            DropdownMenuItem(
                                text = { Text(jobType) },
                                onClick = {
                                    selectedJobType = jobType
                                    showJobTypeDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.featuredJobs.isNotEmpty()) {
                Text(
                    text = "Featured Jobs",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.featuredJobs) { job ->
                        FeaturedJobCard(
                            job = job,
                            onClick = { navController.navigate("jobDetails/${job.id}") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // All jobs section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Jobs",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "${filteredJobs.size} jobs found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (filteredJobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieAnimationView(
                            animationResId = R.raw.addjob,
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No jobs found",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try adjusting your search or filters",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredJobs) { job ->
                        JobCard(
                            job = job,
                            onClick = { navController.navigate("jobDetails/${job.id}") },
                            onSaveClick = {
                                if (viewModel.isJobSaved(job.id)) {
                                    viewModel.removeSavedJob(job.id)
                                } else {
                                    viewModel.saveJob(job)
                                }
                            },
                            isSaved = viewModel.isJobSaved(job.id)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedJobCard(
    job: Job,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = job.company,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Salary",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.salary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("View Details")
                }
            }
        }
    }
}

@Composable
fun JobCard(
    job: Job,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    isSaved: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = job.company,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onSaveClick) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = if (isSaved) "Unsave Job" else "Save Job",
                        tint = if (isSaved) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.location,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Salary",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.salary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Job category and type chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category chip
                AssistChip(
                    onClick = { /* Do nothing */ },
                    label = {
                        Text(
                            try {
                                // Try to convert the category string to enum and get display name
                                val categoryEnum = job.category.let {
                                    try {
                                        JobCategory.valueOf(it)
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                                categoryEnum?.displayName ?: job.category
                            } catch (e: Exception) {
                                job.category
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Job Category",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                // Job type chip
                AssistChip(
                    onClick = { /* Do nothing */ },
                    label = { Text(job.type) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Job Type",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // View details button
                TextButton(onClick = onClick) {
                    Text("View Details")
                }
            }
        }
    }
}


@Composable
fun LottieAnimationView(
    animationResId: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
    isPlaying: Boolean = true
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationResId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = isPlaying
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

