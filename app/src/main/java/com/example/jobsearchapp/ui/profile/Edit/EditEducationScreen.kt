package com.example.jobsearchapp.ui.profile.Edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobsearchapp.ui.profile.DateFieldWithPicker
import com.example.jobsearchapp.ui.profile.Education
import com.example.jobsearchapp.ui.profile.FormField
import com.example.jobsearchapp.ui.profile.RemoveWorkExperienceBottomSheet
import com.example.jobsearchapp.ui.profile.UndoWorkChangesBottomSheet
import kotlinx.coroutines.launch
import com.example.jobsearchapp.ui.profile.DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEducationScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (institution: String, degree: String, graduationDate: String) -> Unit = { _, _, _ -> }
) {
    var institution by remember { mutableStateOf("") }
    var levelOfEducation by remember { mutableStateOf("") }
    var fieldOfStudy by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isCurrentPosition by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }


    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var hasUnsavedChanges by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val undoSheetState = rememberModalBottomSheetState()

    val onBackPressed = {
        if (hasUnsavedChanges) {
            scope.launch { undoSheetState.show() }
        } else {
            onBackClick()
        }
        Unit
    }

    BackHandler(onBack = onBackPressed)

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Close",
                            tint = Color(0xFF2A0F66)
                        )
                    }
                    Text(
                        text = "Add education",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A0F66),
                        modifier = Modifier.weight(1f)
                    )
                }

                TextFieldWithLabel(
                    label = "Level of education",
                    value = levelOfEducation,
                    onValueChange = {
                        levelOfEducation = it
                        hasUnsavedChanges = true
                    }
                )

                TextFieldWithLabel(
                    label = "Institution name",
                    value = institution,
                    onValueChange = {
                        institution = it
                        hasUnsavedChanges = true
                    }
                )

                TextFieldWithLabel(
                    label = "Field of study",
                    value = fieldOfStudy,
                    onValueChange = {
                        fieldOfStudy = it
                        hasUnsavedChanges = true
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DateFieldWithPicker(
                        label = "Start date",
                        value = startDate,
                        onPickerClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )

                    DateFieldWithPicker(
                        label = "End date",
                        value = endDate,
                        onPickerClick = { showEndDatePicker = true },
                        enabled = !isCurrentPosition,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCurrentPosition,
                        onCheckedChange = {
                            isCurrentPosition = it
                            hasUnsavedChanges = true
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

                FormField(
                    label = "Description",
                    value = description,
                    onValueChange = {
                        description = it
                        hasUnsavedChanges = true
                    },
                    singleLine = false,
                    maxLines = 5,
                    minHeight = 120.dp
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val fullDegree = "$levelOfEducation in $fieldOfStudy".trim()
                        val graduationDate = if (isCurrentPosition) startDate else endDate
                        onSaveClick(institution, fullDegree, graduationDate)
                        onBackClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A0F66)),
                ) {
                    Text(
                        text = "SAVE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
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
                hasUnsavedChanges = true
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
                hasUnsavedChanges = true
            }
        )
    }

    if (undoSheetState.isVisible) {
        UndoWorkChangesBottomSheet(
            sheetState = undoSheetState,
            onDismissRequest = { scope.launch { undoSheetState.hide() } },
            onConfirm = {
                scope.launch {
                    undoSheetState.hide()
                }.invokeOnCompletion {
                    onBackClick()
                }
            },
            onUndoChanges = { scope.launch { undoSheetState.hide() } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEducationScreen(
    education: com.example.jobsearchapp.Education,
    onCloseClick: () -> Unit = {},
    onSaveClick: (String, String, String) -> Unit = { _, _, _ -> },
    onRemoveClick: () -> Unit = {}
) {
    val degreeComponents = education.degree.split(" in ")
    val initialLevelOfEducation = if (degreeComponents.isNotEmpty()) degreeComponents[0] else education.degree
    val initialFieldOfStudy = if (degreeComponents.size > 1) degreeComponents[1] else ""

    var institution by remember { mutableStateOf(education.institution) }
    var levelOfEducation by remember { mutableStateOf(initialLevelOfEducation) }
    var fieldOfStudy by remember { mutableStateOf(initialFieldOfStudy) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf(education.graduationDate) }
    var isCurrentPosition by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var hasUnsavedChanges by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val undoSheetState = rememberModalBottomSheetState()
    val removeSheetState = rememberModalBottomSheetState()

    val onBackPressed = {
        if (hasUnsavedChanges) {
            scope.launch { undoSheetState.show() }
        } else {
            onCloseClick()
        }
        Unit
    }

    BackHandler(onBack = onBackPressed)

    Surface(color = Color(0xFFF5F5F5)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF2A0F66)
                        )
                    }
                    Text(
                        text = "Change education",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2A0F66),
                        modifier = Modifier.weight(1f)
                    )
                }
                TextFieldWithLabel(
                    label = "Level of education",
                    value = levelOfEducation,
                    onValueChange = {
                        levelOfEducation = it
                        hasUnsavedChanges = true
                    }
                )

                TextFieldWithLabel(
                    label = "Institution name",
                    value = institution,
                    onValueChange = {
                        institution = it
                        hasUnsavedChanges = true
                    }
                )

                TextFieldWithLabel(
                    label = "Field of study",
                    value = fieldOfStudy,
                    onValueChange = {
                        fieldOfStudy = it
                        hasUnsavedChanges = true
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DateFieldWithPicker(
                        label = "Start date",
                        value = startDate,
                        onPickerClick = { showStartDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )

                    DateFieldWithPicker(
                        label = "End date",
                        value = endDate,
                        onPickerClick = { showEndDatePicker = true },
                        enabled = !isCurrentPosition,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCurrentPosition,
                        onCheckedChange = {
                            isCurrentPosition = it
                            hasUnsavedChanges = true
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

                FormField(
                    label = "Description",
                    value = description,
                    onValueChange = {
                        description = it
                        hasUnsavedChanges = true
                    },
                    singleLine = false,
                    maxLines = 5,
                    minHeight = 120.dp
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { scope.launch { removeSheetState.show() } },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF))
                    ) {
                        Text(
                            text = "REMOVE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            val fullDegree = "$levelOfEducation in $fieldOfStudy".trim()
                            val graduationDate = if (isCurrentPosition) startDate else endDate
                            onSaveClick(institution, fullDegree, graduationDate)
                            onCloseClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A0F66))
                    ) {
                        Text(
                            text = "SAVE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
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
                hasUnsavedChanges = true
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
                hasUnsavedChanges = true
            }
        )
    }

    if (undoSheetState.isVisible) {
        UndoWorkChangesBottomSheet(
            sheetState = undoSheetState,
            onDismissRequest = { scope.launch { undoSheetState.hide() } },
            onConfirm = {
                scope.launch {
                    undoSheetState.hide()
                }.invokeOnCompletion {
                    onCloseClick()
                }
            },
            onUndoChanges = { scope.launch { undoSheetState.hide() } }
        )
    }

    if (removeSheetState.isVisible) {
        RemoveWorkExperienceBottomSheet(
            onDismissRequest = { scope.launch { removeSheetState.hide() } },
            onConfirm = {
                scope.launch {
                    removeSheetState.hide()
                }.invokeOnCompletion {
                    onRemoveClick()
                }
            }
        )
    }
}

@Composable
fun TextFieldWithLabel(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    maxLines: Int = 1
) {
    Text(
        text = label,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedBorderColor = Color(0xFF2A0F66)
        ),
        placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder) } } else null,
        maxLines = maxLines
    )
}



//@Preview(showBackground = true)
//@Composable
//fun AddEducationScreenPreview() {
//    AddEducationScreen()
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ChangeEducationScreenPreview() {
//    val sampleEducation = Education(
//        id = 1,
//        userId = 1,
//        institution = "University of Oxford",
//        degree = "Bachelor of Information Technology in Information Technology",
//        graduationDate = "Aug 2013"
//    )
//    ChangeEducationScreen(education = sampleEducation)
//}

