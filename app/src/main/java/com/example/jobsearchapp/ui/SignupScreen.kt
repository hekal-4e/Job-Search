package com.example.jobsearchapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jobsearchapp.AuthState
import com.example.jobsearchapp.AuthViewModel
import com.example.jobsearchapp.BackgroundContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val authState by viewModel.authState.observeAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    // For dropdown menu
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Non-binary", "Prefer not to say")
    // For validation
    var isFormValid by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }

    // Validation function
    fun validateForm() {
        // Reset errors
        nameError = null
        emailError = null
        passwordError = null
        ageError = null
        // Validate name
        if (name.isBlank()) {
            nameError = "Name is required"
        }
        // Validate email
        if (email.isBlank()) {
            emailError = "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Please enter a valid email address"
        }
        // Validate password
        if (password.isBlank()) {
            passwordError = "Password is required"
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
        }
        // Validate age
        if (age.isNotBlank()) {
            try {
                val ageValue = age.toInt()
                if (ageValue < 16 || ageValue > 100) {
                    ageError = "Age must be between 16 and 100"
                }
            } catch (e: NumberFormatException) {
                ageError = "Please enter a valid age"
            }
        }
        // Check if form is valid
        isFormValid = nameError == null && emailError == null &&
                passwordError == null && ageError == null &&
                termsAccepted
    }

    // Validate form whenever any field changes
    LaunchedEffect(name, email, password, age, termsAccepted) {
        validateForm()
    }

    val scrollState = rememberScrollState()
    BackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Create an Account",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                "Ready to take the next step in your career?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Display verification email sent message
            if (authState is AuthState.VerificationEmailSent) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Verification Email Sent!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "We've sent a verification email to $email. Please check your inbox and verify your email address to continue.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { viewModel.resendVerificationEmail() }
                            ) {
                                Text("Resend Email")
                            }
                            Button(
                                onClick = { viewModel.refreshVerificationStatus() }
                            ) {
                                Text("I've Verified")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { navController.navigate("login") }
                        ) {
                            Text("Go to Login")
                        }
                    }
                }
            } else {
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = {
                        emailError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError != null,
                    supportingText = {
                        passwordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Age field
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = ageError != null,
                    supportingText = {
                        ageError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Gender dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Gender dropdown"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Terms and conditions checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text(
                    "I agree to the Terms of Service and Privacy Policy",
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Sign up button
            Button(
                onClick = {
                    if (isFormValid) {
                        viewModel.signupWithProfile(email, password, name, age, gender)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isFormValid
            ) {
                Text("SIGN UP")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Already have an account?", color = Color.White)
                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Sign in", color = Color.LightGray)
                }
            }
        }

        // React to Auth State
        when (authState) {
            is AuthState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is AuthState.Error -> {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Error") },
                    text = { Text((authState as AuthState.Error).message) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.checkAuthStatus() }) {
                            Text("OK")
                        }
                    }
                )
            }
            is AuthState.Authenticated -> {
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
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
