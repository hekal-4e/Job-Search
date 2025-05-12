package com.example.jobsearchapp.ui.profile


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UpdatePasswordScreen(onBack: () -> Unit) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                text = "Update Password",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Old Password",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (oldPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                    Icon(
                        imageVector = if (oldPasswordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = if (oldPasswordVisible) "Hide password"
                        else "Show password"
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF1E275B)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "New Password",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (newPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = if (newPasswordVisible) "Hide password"
                        else "Show password"
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF1E275B)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Confirm Password",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Hide password"
                        else "Show password"
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFF1E275B)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {  },
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
                text = "UPDATE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}