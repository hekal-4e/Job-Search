package com.example.jobsearchapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.jobsearchapp.R

@Composable
fun ApplicationSuccessScreen(
    navController: NavController,
    jobTitle: String,
    companyName: String
) {
    // Success animation
    val compositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.success1)
    )
    val progress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        isPlaying = true,
        iterations = LottieConstants.IterateForever,
        speed = 0.8f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Success animation
            LottieAnimation(
                composition = compositionResult.value,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Success message
            Text(
                text = "Application Submitted!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your application for $jobTitle at $companyName has been submitted successfully.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You will be notified when the recruiter reviews your application.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Return to home button
            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return to Home")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // View applications button
            OutlinedButton(
                onClick = {
                    navController.navigate("myApplications")
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text("View My Applications")
            }
        }
    }
}
