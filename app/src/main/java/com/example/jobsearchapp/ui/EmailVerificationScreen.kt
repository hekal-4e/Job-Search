package com.example.jobsearchapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.jobsearchapp.AuthState
import com.example.jobsearchapp.AuthViewModel
import com.example.jobsearchapp.BackgroundContainer
import com.example.jobsearchapp.R

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val authState by viewModel.authState.observeAsState()
    val userEmail = viewModel.getCurrentUserEmail()

    // Check verification status periodically
    LaunchedEffect(Unit) {
        // Check every 5 seconds if the user has verified their email
        while (true) {
            kotlinx.coroutines.delay(5000)
            viewModel.refreshVerificationStatus()
        }
    }

    BackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Email icon
            Icon(
                painter = painterResource(id = R.drawable.email), // Make sure to add this resource
                contentDescription = "Email Verification",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Verify Your Email",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "We've sent a verification email to:\n$userEmail",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Please check your inbox and click the verification link to complete your registration.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.resendVerificationEmail() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("RESEND VERIFICATION EMAIL")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.refreshVerificationStatus() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("I'VE VERIFIED MY EMAIL")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    viewModel.signout()
                    navController.navigate("login") {
                        popUpTo("verifyEmail") { inclusive = true }
                    }
                }
            ) {
                Text("BACK TO LOGIN", color = Color.LightGray)
            }

            // React to Auth State
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                is AuthState.Error -> {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                is AuthState.Authenticated -> {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo("verifyEmail") { inclusive = true }
                        }
                    }
                }
                is AuthState.VerificationEmailSent -> {
                    Text(
                        "Verification email sent! Please check your inbox.",
                        color = Color.Green,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {}
            }
        }
    }
}
