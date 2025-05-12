package com.example.jobsearchapp.ui.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import java.time.format.DateTimeFormatter

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minHeight: Dp = 56.dp
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder) },
            singleLine = singleLine,
            maxLines = maxLines,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E0F5C),
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun DateFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = {  },
            placeholder = { Text("Select date") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select date",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1E0F5C),
                unfocusedBorderColor = Color.LightGray
            )
        )

        if (showDatePicker) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Column {
                    val examples = listOf("Jan 2023", "Feb 2023", "Mar 2023", "Apr 2023", "Present")
                    examples.forEach { date ->
                        Text(
                            text = date,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(date)
                                    showDatePicker = false
                                }
                                .padding(12.dp)
                        )
                    }
                    // Cancel option
                    Text(
                        text = "Cancel",
                        color = Color(0xFF1E0F5C),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { showDatePicker = false }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

// Need to import this for the SolidColor brush
//private class SolidColor(val color: Color) : androidx.compose.ui.graphics.Brush {
//    override fun applyTo(size: Size, alpha: Float): androidx.compose.ui.graphics.Brush = this
//}