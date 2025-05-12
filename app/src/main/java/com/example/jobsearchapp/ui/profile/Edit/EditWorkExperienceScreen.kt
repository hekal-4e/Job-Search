package com.example.jobsearchapp.ui.Edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.jobsearchapp.WorkExperience
import com.example.jobsearchapp.ui.profile.DateFieldWithPicker
import com.example.jobsearchapp.ui.profile.DatePickerDialog
import com.example.jobsearchapp.ui.profile.FormField
import com.example.jobsearchapp.ui.profile.RemoveWorkExperienceBottomSheet
import com.example.jobsearchapp.ui.profile.UndoWorkChangesBottomSheet

// Define Purple80 color if it's not defined elsewhere
private val Purple80 = Color(0xFFD0BCFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkExperienceScreen(
    onBack: () -> Unit,
    onSave: (WorkExperience) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var jobTitle by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCurrentPosition by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    var showUndoChangesDialog by remember { mutableStateOf(false) }
    val undoChangesSheetState = rememberModalBottomSheetState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    fun hasUnsavedChanges(): Boolean {
        return (
                jobTitle.isNotEmpty() ||
                        company.isNotEmpty() ||
                        startDate.isNotEmpty() ||
                        endDate.isNotEmpty() ||
                        description.isNotEmpty()
                )
    }

    val onBackPressed = {
        if (hasUnsavedChanges()) {
            showUndoChangesDialog = true
        } else {
            onBack()
        }
        Unit
    }

    BackHandler(onBack = onBackPressed)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF2A0F66)
                )
            }
            Text(
                text = "Add work experience",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = Color(0xFF2A0F66),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        FormField(
            label = "Job title",
            value = jobTitle,
            onValueChange = { jobTitle = it },
            placeholder = "Enter your position here"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = "Company",
            value = company,
            onValueChange = { company = it },
            placeholder = "Enter company name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DateFieldWithPicker(
                label = "Start date",
                value = startDate,
                onPickerClick = { showStartDatePicker = true },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            DateFieldWithPicker(
                label = "End date",
                value = endDate,
                onPickerClick = { showEndDatePicker = true },
                enabled = !isCurrentPosition,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isCurrentPosition,
                onCheckedChange = { isCurrentPosition = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2A0F66),
                    uncheckedColor = Color(0xFF757575)
                )
            )
            Text(
                text = "This is my position now",
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = "Description",
            value = description,
            onValueChange = { description = it },
            placeholder = "Briefly describe your role and responsibilities",
            singleLine = false,
            maxLines = 5,
            minHeight = 120.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val finalEndDate = if (isCurrentPosition) "Present" else endDate
                val newWorkExperience = WorkExperience(
                    userId = 1,
                    position = jobTitle,
                    startDate = startDate,
                    endDate = finalEndDate,
                    description = description,
                    isCurrentJob = isCurrentPosition,
                    company = company,
                )
                onSave(newWorkExperience)
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E0F5C),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "SAVE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            title = "Start Date",
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { month, year ->
                startDate = "$month $year"
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            title = "End Date",
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { month, year ->
                endDate = "$month $year"
                showEndDatePicker = false
            }
        )
    }

    if (showUndoChangesDialog) {
        UndoWorkChangesBottomSheet(
            onDismissRequest = { showUndoChangesDialog = false },
            onConfirm = {
                showUndoChangesDialog = false
                onBack()
            },
            onUndoChanges = {
                showUndoChangesDialog = false
            },
            sheetState = undoChangesSheetState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeWorkExperienceScreen(
    workExperiences: List<WorkExperience>,
    onWorkExperienceUpdated: (List<WorkExperience>) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentExperience = workExperiences.firstOrNull() ?: WorkExperience(
        id = 1,
        userId = 1,
        position = "",
        startDate = "",
        endDate = "",
        description = "",
        isCurrentJob = false,
        company = "",
    )

    val originalJobTitle = remember { mutableStateOf(currentExperience.position) }
    val originalCompany = remember { mutableStateOf(currentExperience.company) }
    val originalStartDate = remember { mutableStateOf(currentExperience.startDate) }
    val originalEndDate = remember { mutableStateOf(currentExperience.endDate ?: "") }
    val originalDescription = remember { mutableStateOf(currentExperience.description) }

    var jobTitle by remember { mutableStateOf(currentExperience.position) }
    var company by remember { mutableStateOf(currentExperience.company) }
    var startDate by remember { mutableStateOf(currentExperience.startDate) }
    var endDate by remember { mutableStateOf(currentExperience.endDate ?: "") }
    var description by remember { mutableStateOf(currentExperience.description) }
    var isCurrentPosition by remember { mutableStateOf(currentExperience.isCurrentJob) }

    val scrollState = rememberScrollState()
    var showRemoveDialog by remember { mutableStateOf(false) }
    var showUndoChangesBottomSheet by remember { mutableStateOf(false) }
    val undoChangesSheetState = rememberModalBottomSheetState()
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    fun hasUnsavedChanges(): Boolean {
        return (
                jobTitle != originalJobTitle.value ||
                        company != originalCompany.value ||
                        startDate != originalStartDate.value ||
                        endDate != originalEndDate.value ||
                        description != originalDescription.value ||
                        isCurrentPosition != currentExperience.isCurrentJob
                )
    }

    val onBackPressed = {
        if (hasUnsavedChanges()) {
            showUndoChangesBottomSheet = true
        } else {
            navController.popBackStack()
        }
        Unit
    }

    BackHandler(onBack = onBackPressed)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF2A0F66)
                )
            }
            Text(
                text = "Change work experience",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = Color(0xFF2A0F66),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        FormField(
            label = "Job title",
            value = jobTitle,
            onValueChange = { jobTitle = it },
            placeholder = "Enter your position here"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = "Company",
            value = company,
            onValueChange = { company = it },
            placeholder = "Enter company name"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DateFieldWithPicker(
                label = "Start date",
                value = startDate,
                onPickerClick = { showStartDatePicker = true },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            DateFieldWithPicker(
                label = "End date",
                value = if (isCurrentPosition as Boolean) "Present" else endDate,
                onPickerClick = { showEndDatePicker = true },
                enabled = !(isCurrentPosition as Boolean),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isCurrentPosition as Boolean,
                onCheckedChange = {
                    isCurrentPosition = it
                    if (it) {
                        endDate = "Present"
                    } else if (endDate == "Present") {
                        endDate = ""
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2A0F66),
                    uncheckedColor = Color(0xFF757575)
                )
            )
            Text(
                text = "This is my position now",
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FormField(
            label = "Description",
            value = description,
            onValueChange = { description = it },
            placeholder = "Briefly describe your role and responsibilities",
            singleLine = false,
            maxLines = 5,
            minHeight = 120.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { showRemoveDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple80,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = "REMOVE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    val finalEndDate = if (isCurrentPosition as Boolean) "Present" else endDate
                    val updatedExperience = WorkExperience(
                        id = currentExperience.id,
                        userId = currentExperience.userId,
                        position = jobTitle,
                        startDate = startDate,
                        endDate = finalEndDate,
                        description = description,
                        isCurrentJob = isCurrentPosition,
                        company = company,
                    )

                    val updatedList = if (workExperiences.isEmpty()) {
                        listOf(updatedExperience)
                    } else {
                        workExperiences.map {
                            if (it.id == currentExperience.id) updatedExperience else it
                        }
                    }

                    onWorkExperienceUpdated(updatedList)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E0F5C),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = "SAVE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerDialog(
            title = "Start Date",
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { month, year ->
                startDate = "$month $year"
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        DatePickerDialog(
            title = "End Date",
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { month, year ->
                endDate = "$month $year"
                showEndDatePicker = false
            }
        )
    }

    if (showUndoChangesBottomSheet) {
        UndoWorkChangesBottomSheet(
            sheetState = undoChangesSheetState,
            onDismissRequest = { showUndoChangesBottomSheet = false },
            onConfirm = {
                showUndoChangesBottomSheet = false
                navController.popBackStack()
            },
            onUndoChanges = {
                showUndoChangesBottomSheet = false
                jobTitle = originalJobTitle.value
                company = originalCompany.value
                startDate = originalStartDate.value
                endDate = originalEndDate.value
                description = originalDescription.value
                isCurrentPosition = currentExperience.isCurrentJob
            }
        )
    }

    if (showRemoveDialog) {
        RemoveWorkExperienceBottomSheet(
            onConfirm = {
                // Remove the current experience from the list
                val updatedList = workExperiences.filter { it.id != currentExperience.id }
                onWorkExperienceUpdated(updatedList)
                navController.popBackStack()
            },
            onDismissRequest = { showRemoveDialog = false }
        )
    }
}
