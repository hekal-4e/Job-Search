package com.example.jobsearchapp.ui.profile.Edit


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobsearchapp.ui.profile.UndoWorkChangesBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAboutScreen(
    initialAboutText: String = "",
    onBackClick: () -> Unit = {},
    onSaveClick: (String) -> Unit = {}
) {
    var aboutText by remember { mutableStateOf(initialAboutText) }
    val hasChanges = aboutText != initialAboutText

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val handleBackPress = {
        if (hasChanges) {
            showBottomSheet = true
        } else {
            onBackClick()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { handleBackPress() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2A0F66)
                    )
                }
                Text(
                    text = "Edit About me",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp),
                    color = Color(0xFF2A0F66),
                )
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                TextField(
                    value = aboutText,
                    onValueChange = { aboutText = it },
                    placeholder = { Text("Tell me about you.") },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSaveClick(aboutText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A0F66)
                )
            ) {
                Text(
                    text = "SAVE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showBottomSheet) {
        UndoWorkChangesBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false },
            onConfirm = {
                showBottomSheet = false
                onBackClick()
            },
            onUndoChanges = {
                showBottomSheet = false
                onBackClick()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditAboutScreenPreview() {
    EditAboutScreen()
}