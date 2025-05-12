package com.example.jobsearchapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jobsearchapp.AuthState
import com.example.jobsearchapp.AuthViewModel
import com.example.jobsearchapp.BackgroundContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val authState by viewModel.authState.observeAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    BackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Welcome Back",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                "Sign in to continue",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* TODO: Implement forgot password */ }) {
                    Text("Forgot Password?", color = Color.LightGray)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("SIGN IN")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Don't have an account?", color = Color.White)
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text("Sign up", color = Color.LightGray)
                }
            }

            // React to Auth State
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is AuthState.Authenticated -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                is AuthState.EmailNotVerified -> {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("Email Verification Required") },
                        text = {
                            Text("Please verify your email address before continuing. Check your inbox for a verification link.")
                        },
                        confirmButton = {
                            TextButton(onClick = { viewModel.resendVerificationEmail() }) {
                                Text("Resend Email")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.refreshVerificationStatus() }) {
                                Text("I've Verified")
                            }
                        }
                    )
                }
                else -> {}
            }
        }
    }
}
