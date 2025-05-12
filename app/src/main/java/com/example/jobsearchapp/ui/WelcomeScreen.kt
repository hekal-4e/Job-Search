package com.example.jobsearchapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jobsearchapp.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    var currentPage by remember { mutableStateOf(0) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LaunchedEffect(currentPage) {
        if (currentPage == 3) {
            delay(2000)
            navController.navigate("login") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Use AnimatedContent to transition between pages
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                // Slide in from right, slide out to left
                slideInHorizontally(initialOffsetX = { it }) with
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) { page ->
            when (page) {
                0 -> WelcomePage1(onNext = { currentPage = 1 })
                1 -> WelcomePage2(onNext = { currentPage = 2 })
                2 -> WelcomePage3(onNext = { currentPage = 3 })
                3 -> FinalWelcomePage()
            }
        }

        // Page indicators
        if (currentPage < 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (index == currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomePage1(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimationView(
            animationResId = R.raw.appanime,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Find Your Dream Job",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Discover thousands of job opportunities with all the information you need",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("Next")
        }
    }
}

@Composable
fun WelcomePage2(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimationView(
            animationResId = R.raw.addjob,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Easy Application Process",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Apply for jobs with just a few taps and track your application status in real-time",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("Next")
        }
    }
}

@Composable
fun WelcomePage3(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimationView(
            animationResId = R.raw.appanime,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Get Notified",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Receive notifications about application updates, interview invitations, and more",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(50.dp)
        ) {
            Text("Get Started")
        }
    }
}

@Composable
fun FinalWelcomePage() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            LottieAnimationView(
                animationResId = R.raw.rocket,
                modifier = Modifier.size(300.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Welcome to JobVerse!",
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.scale(scale)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "A Universe Full Of Job Opportunities",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
