package com.example.jobsearchapp.ui.profile


import OrangeTheme.Purple80
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutBottomSheet(
    onDismissRequest: () -> Unit,
    onLogoutConfirmed: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Log out",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Are you sure you want to leave?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Button(
                onClick = {
                    onLogoutConfirmed()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E275B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple80,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UndoWorkChangesBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onUndoChanges: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Undo Changes?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Are you sure you want to undo your changes?",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E0F5C),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "CONTINUE TO UNDO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple80,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "CANCEL",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveWorkExperienceBottomSheet(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Remove Work Experience?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Are you sure you want to delete this experience?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
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
                    text = "CONTINUE TO DELETE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple80,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "CANCEL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DateFieldWithPicker(
    label: String,
    value: String,
    onPickerClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = {  },
            readOnly = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF2A0F66),
                disabledContainerColor = Color(0xFFF5F5F5),
                disabledBorderColor = Color(0xFFE0E0E0)
            ),
            placeholder = { Text("Sep 2010") },
            trailingIcon = {
                if (enabled) {
                    IconButton(onClick = onPickerClick) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            tint = Color(0xFF2A0F66)
                        )
                    }
                }
            }
        )
    }
}



@Composable

fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (month: String, year: String) -> Unit,
    title: String
) {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val currentYear = java.time.Year.now().value
    val years = (currentYear downTo currentYear - 50).map { it.toString() }

    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Date",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2A0F66),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Month selection
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(months) { month ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedMonth = month }
                                            .background(
                                                if (selectedMonth == month)
                                                    Color(0xFFE0E0FF)
                                                else
                                                    Color.Transparent
                                            )
                                            .padding(vertical = 12.dp, horizontal = 16.dp)
                                    ) {
                                        Text(
                                            text = month,
                                            color = if (selectedMonth == month)
                                                Color(0xFF2A0F66)
                                            else
                                                Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Year selection
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Year",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F5F5))
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(years) { year ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedYear = year }
                                            .background(
                                                if (selectedYear == year)
                                                    Color(0xFFE0E0FF)
                                                else
                                                    Color.Transparent
                                            )
                                            .padding(vertical = 12.dp, horizontal = 16.dp)
                                    ) {
                                        Text(
                                            text = year,
                                            color = if (selectedYear == year)
                                                Color(0xFF2A0F66)
                                            else
                                                Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (selectedMonth.isNotEmpty() && selectedYear.isNotEmpty()) {
                                onDateSelected(selectedMonth, selectedYear)
                            }
                        },
                        enabled = selectedMonth.isNotEmpty() && selectedYear.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A0F66)
                        )
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}

@Composable
fun CircularWheelPicker(
    items: List<String>,
    initialSelectedItem: String,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 50.dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(itemHeight * 3)
                .fillMaxWidth()
        ) {
            val initialIndex = items.indexOf(initialSelectedItem).coerceIn(0, items.size - 1)
            var selectedIndex by remember { mutableStateOf(initialIndex) }

            LaunchedEffect(selectedIndex) {
                onSelectionChanged(items[selectedIndex])
            }

            if (selectedIndex > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .align(Alignment.TopCenter)
                        .clickable { selectedIndex = (selectedIndex - 1).coerceIn(0, items.size - 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[selectedIndex - 1],
                        fontSize = 18.sp,
                        color = Color.Gray.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(0xFFFF9E45))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[selectedIndex],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            if (selectedIndex < items.size - 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .align(Alignment.BottomCenter)
                        .clickable { selectedIndex = (selectedIndex + 1).coerceIn(0, items.size - 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[selectedIndex + 1],
                        fontSize = 18.sp,
                        color = Color.Gray.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun EducationScreenBottomButtons(
    onRemoveClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Remove button
        Button(
            onClick = onRemoveClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBBAAE3))
        ) {
            Text(
                text = "REMOVE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        // Save button
        Button(
            onClick = onSaveClick,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerExample() {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Aug 2013") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Change Education",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2A0F66),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sample fields
            OutlinedTextField(
                value = "Bachelor of Information Technology",
                onValueChange = {},
                label = { Text("Level of education") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                label = { Text("End date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Text("📅")
                    }
                }
            )
        }

        // Bottom buttons
        EducationScreenBottomButtons()
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { month, year ->
                selectedDate = "$month $year"
                showDatePicker = false
            },
            title = "Start Date"
        )
    }
}



