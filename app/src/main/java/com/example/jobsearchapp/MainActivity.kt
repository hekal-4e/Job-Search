package com.example.jobsearchapp
import com.example.jobsearchapp.viewmodel.ProfileViewModel
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.jobsearchapp.ui.*
import com.example.jobsearchapp.ui.Edit.AddLanguageScreen
import com.example.jobsearchapp.ui.Edit.AddWorkExperienceScreen
import com.example.jobsearchapp.ui.Edit.ChangeWorkExperienceScreen
import com.example.jobsearchapp.ui.Edit.EditLanguageScreen
import com.example.jobsearchapp.ui.Edit.LanguageScreen
import com.example.jobsearchapp.ui.profile.AddAppreciationScreen
import com.example.jobsearchapp.ui.profile.AppreciationScreen
import com.example.jobsearchapp.ui.profile.ChangeEducationListScreen
import com.example.jobsearchapp.ui.proposalscreen
import com.example.jobsearchapp.ui.profile.Edit.*
import com.example.jobsearchapp.ui.profile.EditWorkExperienceScreen
import com.example.jobsearchapp.ui.profile.ProfileScreen
import com.example.jobsearchapp.ui.profile.SettingsScreen
import com.example.jobsearchapp.ui.theme.JobSearchAppTheme
import com.example.jobsearchapp.ui.theme.ThemeViewModel
import com.example.jobsearchapp.util.ImageUploadHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    companion object {
        const val PREFS_NAME = "JobSearchAppPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn && FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val appViewModel: AppViewModel = viewModel()
            val isPurpleTheme = themeViewModel.isPurpleTheme.collectAsState(initial = false).value

            JobSearchAppTheme(isPurpleTheme = isPurpleTheme) {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                val authViewModel: AuthViewModel = viewModel()

                // Animation state for transitions
                val animationState = remember { mutableStateOf(AnimationState.IDLE) }
                val transitionProgress = remember { Animatable(0f) }

                // Track previous and current routes for animation
                val previousRoute = remember { mutableStateOf<String?>(null) }
                val currentRouteState = remember { mutableStateOf<String?>(null) }

                // Update route states for transition
                LaunchedEffect(currentRoute) {
                    if (currentRoute != currentRouteState.value) {
                        previousRoute.value = currentRouteState.value
                        currentRouteState.value = currentRoute
                        // Trigger animation
                        animationState.value = AnimationState.ANIMATING
                        transitionProgress.snapTo(0f)
                        transitionProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(2000, easing = FastOutSlowInEasing)
                        )
                        animationState.value = AnimationState.IDLE
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { paddingValues ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                        ) {
                            // Main content with animated NavHost
                            AnimatedNavHost(
                                navController = navController,
                                startDestination = "welcome",
                                transitionProgress = transitionProgress.value,
                                animationState = animationState.value
                            ) {
                                composable("welcome") {
                                    WelcomeScreen(navController = navController)
                                }
                                composable("login") {
                                    LoginScreen(
                                        navController = navController,
                                        viewModel = authViewModel
                                    )
                                }
                                composable("signup") {
                                    SignUpScreen(
                                        navController = navController,
                                        viewModel = authViewModel
                                    )
                                }
                                composable("verifyEmail") {
                                    EmailVerificationScreen(
                                        navController = navController,
                                        viewModel = authViewModel
                                    )
                                }
                                composable("home") {
                                    val isUserLoggedIn = remember {
                                        mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
                                    }
                                    // Check if user is actually logged in
                                    LaunchedEffect(Unit) {
                                        if (!isUserLoggedIn.value) {
                                            navController.navigate("welcome") {
                                                popUpTo(navController.graph.id) { inclusive = true }
                                            }
                                        }
                                    }
                                    HomeScreen(
                                        navController = navController,
                                        viewModel = appViewModel,
                                        onThemeChange = { themeViewModel.toggleTheme() },
                                        onLogout = {
                                            // Perform logout
                                            authViewModel.signout()
                                            // Navigate to welcome screen
                                            navController.navigate("welcome") {
                                                popUpTo(navController.graph.id) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable(
                                    route = "jobDetails/{jobId}",
                                    arguments = listOf(navArgument("jobId") {
                                        type = NavType.StringType
                                    })
                                ) { backStackEntry ->
                                    val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                                    JobDetailsScreen(
                                        navController = navController,
                                        viewModel = appViewModel,
                                        jobId = jobId
                                    )
                                }
                                composable(
                                    route = "proposal/{jobId}",
                                    arguments = listOf(navArgument("jobId") {
                                        type = NavType.StringType
                                    })
                                ) { backStackEntry ->
                                    val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                                    val job = appViewModel.jobs.find { it.id == jobId }
                                    if (job != null) {
                                        proposalscreen(
                                            navController = navController,
                                            viewModel = appViewModel,
                                            job = job,
                                        )
                                    } else {
                                        Text(
                                            text = "Job not found",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                                // Inside your NavHost builder in MainActivity.kt
                                composable("applicationSuccess") {
                                    // Get the job title and company from the navigation arguments
                                    val jobTitle = navController.previousBackStackEntry
                                        ?.savedStateHandle?.get<String>("jobTitle") ?: "the job"
                                    val companyName = navController.previousBackStackEntry
                                        ?.savedStateHandle?.get<String>("companyName")
                                        ?: "the company"
                                    ApplicationSuccessScreen(
                                        navController = navController,
                                        jobTitle = jobTitle,
                                        companyName = companyName
                                    )
                                }
                                composable("savedJobs") {
                                    SavedJobsScreen(
                                        navController = navController,
                                        savedJobs = appViewModel.savedJobs
                                    )
                                }
                                composable("myApplications") {
                                    UserApplicationsScreen(
                                        navController = navController,
                                        viewModel = appViewModel
                                    )
                                }
                                composable(
                                    route = "recruiterProposals/{jobId}",
                                    arguments = listOf(navArgument("jobId") {
                                        type = NavType.StringType
                                    })
                                ) { backStackEntry ->
                                    val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                                    RecruiterProposalsScreen(
                                        navController = navController,
                                        viewModel = appViewModel,
                                        jobId = jobId
                                    )
                                }
                                composable("poster") {
                                    PosterScreen(
                                        navController = navController,
                                        viewModel = appViewModel
                                    )
                                }
                                composable("addJob") {
                                    AddJobDialog(
                                        navController = navController,
                                        viewModel = appViewModel
                                    )
                                }
                                composable("notifications") {
                                    NotificationsScreen(
                                        navController = navController,
                                        viewModel = appViewModel
                                    )
                                }
                                composable("chats") {
                                    ChatsListScreen(
                                        navController = navController,
                                        viewModel = appViewModel
                                    )
                                }
                                composable(
                                    route = "chat/{chatId}",
                                    arguments = listOf(navArgument("chatId") {
                                        type = NavType.StringType
                                    })
                                ) { backStackEntry ->
                                    val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                                    ChatScreen(
                                        navController = navController,
                                        viewModel = appViewModel,
                                        chatId = chatId
                                    )
                                }

                                // Inside your NavHost
                                composable("profile") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()
                                    val isLoading by profileViewModel.isLoading.collectAsState(initial = true)
                                    val error by profileViewModel.error.collectAsState(initial = null)

                                    if (isLoading) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    } else if (error != null) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = error ?: "Unknown error",
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(16.dp)
                                                )
                                                Button(onClick = { navController.popBackStack() }) {
                                                    Text("Go Back")
                                                }
                                            }
                                        }
                                    } else if (userProfileState != null) {
                                        ProfileScreen(
                                            userProfile = userProfileState!!,
                                            navController = navController,
                                            onEditClick = {
                                                navController.navigate("edit_profile")
                                            },
                                            onSettingsClick = {
                                                navController.navigate("settings")
                                            },
                                            onProfileImageuploaded = { downloadUrl ->
                                                ImageUploadHelper(downloadUrl)
                                            }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Loading profile...")
                                        }
                                    }
                                }

                                composable("edit_about_me") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        EditAboutScreen(
                                            initialAboutText = userProfileState!!.aboutMe,
                                            onBackClick = { navController.popBackStack() },
                                            onSaveClick = { newAboutText ->
                                                profileViewModel.updateAboutMe(newAboutText)
                                                navController.popBackStack()
                                            }
                                        )
                                    }
                                }

                                composable("change_work_experience") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        ChangeWorkExperienceScreen(
                                            workExperiences = userProfileState!!.workExperience,
                                            onWorkExperienceUpdated = { updatedList ->
                                                // This will be handled by the individual add/edit screens
                                                navController.popBackStack()
                                            },
                                            navController = navController
                                        )
                                    }
                                }
                                composable("settings") {
                                    SettingsScreen(navController = navController)
                                }
                                composable("add_work_experience") {
                                    val profileViewModel: ProfileViewModel = viewModel()

                                    AddWorkExperienceScreen(
                                        onBack = { navController.popBackStack() },
                                        onSave = { newWorkExperience ->
                                            profileViewModel.addWorkExperience(newWorkExperience)
                                            navController.popBackStack()
                                        },
                                        navController = navController
                                    )
                                }

                                composable("add_education") {
                                    val profileViewModel: ProfileViewModel = viewModel()

                                    AddEducationScreen(
                                        onBackClick = { navController.popBackStack() },
                                        onSaveClick = { institution, degree, graduationDate ->
                                            profileViewModel.addEducation(
                                                institution,
                                                degree,
                                                graduationDate
                                            )
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                composable(
                                    "edit_education/{educationId}",
                                    arguments = listOf(navArgument("educationId") {
                                        type = NavType.LongType
                                    })
                                ) { backStackEntry ->
                                    val educationId =
                                        backStackEntry.arguments?.getLong("educationId") ?: 0L
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        val education =
                                            userProfileState!!.education.firstOrNull { it.id == educationId }
                                        if (education != null) {
                                            ChangeEducationScreen(
                                                education = education,
                                                onCloseClick = { navController.popBackStack() },
                                                onSaveClick = { institution, degree, graduationDate ->
                                                    profileViewModel.updateEducation(
                                                        educationId,
                                                        institution,
                                                        degree,
                                                        graduationDate
                                                    )
                                                    navController.popBackStack()
                                                },
                                                onRemoveClick = {
                                                    profileViewModel.deleteEducation(educationId)
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                    }
                                }

                                composable("edit_skills") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        SkillScreen(
                                            skills = userProfileState!!.skills,
                                            onEditClick = { navController.navigate("add_skills") },
                                            onBackClick = { navController.popBackStack() },
                                            onRemoveSkill = { skill ->
                                                profileViewModel.deleteSkill(skill.id)
                                            }
                                        )
                                    }
                                }

                                composable("add_skills") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        AddSkillScreen(
                                            availableSkills = listOf(
                                                "Kotlin",
                                                "Java",
                                                "Android",
                                                "iOS",
                                                "Swift",
                                                "Flutter",
                                                "React Native",
                                                "JavaScript",
                                                "TypeScript",
                                                "HTML",
                                                "CSS",
                                                "SQL",
                                                "Firebase",
                                                "AWS",
                                                "Git",
                                                "Scrum",
                                                "Agile"
                                            ).map { skillName ->
                                                Skill(
                                                    id = 0,
                                                    userId = userProfileState!!.id,
                                                    skillName = skillName
                                                )
                                            },
                                            userSkills = userProfileState!!.skills,
                                            onSkillAdded = { newSkill ->
                                                profileViewModel.addSkill(newSkill.skillName)
                                            },
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }
                                }

                                composable("edit_languages") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        LanguageScreen(
                                            languages = userProfileState!!.languages,
                                            onAddLanguageClick = { navController.navigate("add_language") },
                                            onEditLanguageClick = { language ->
                                                navController.navigate("edit_language/${language.id}")
                                            },
                                            onDeleteLanguage = { language ->
                                                profileViewModel.deleteLanguage(language.id)
                                            },
                                            onSaveClick = { navController.popBackStack() }
                                        )
                                    }
                                }

                                composable("add_language") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        AddLanguageScreen(
                                            availableLanguages = listOf(
                                                "English", "Indonesian", "Malaysian", "French",
                                                "German", "Hindi", "Italian", "Japanese"
                                            ),
                                            onLanguageSelected = { selectedLanguage ->
                                                val newLanguage = Language(
                                                    id = System.currentTimeMillis(),
                                                    userId = userProfileState!!.id,
                                                    languageName = selectedLanguage,
                                                    languageLevel = "0,0",
                                                )
                                                // Navigate to edit screen for the new language
                                                navController.navigate("edit_language/${newLanguage.id}")
                                            },
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }
                                }

                                composable(
                                    route = "edit_language/{languageId}",
                                    arguments = listOf(navArgument("languageId") {
                                        type = NavType.LongType
                                    })
                                ) { backStackEntry ->
                                    val languageId =
                                        backStackEntry.arguments?.getLong("languageId") ?: 0L
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        val language =
                                            userProfileState!!.languages.find { it.id == languageId }
                                        if (language != null) {
                                            EditLanguageScreen(
                                                language = language,
                                                onBackClick = { navController.popBackStack() },
                                                onSaveClick = { oral, written ->
                                                    val languageLevel = "$oral,$written"
                                                    profileViewModel.updateLanguage(
                                                        languageId,
                                                        language.languageName,
                                                        languageLevel
                                                    )
                                                    navController.popBackStack()
                                                }
                                            )
                                        } else {
                                            // Handle case for new language
                                            val availableLanguages = listOf(
                                                "English", "Indonesian", "Malaysian", "French",
                                                "German", "Hindi", "Italian", "Japanese"
                                            )
                                            // Find the selected language from the previous screen
                                            val selectedLanguage =
                                                availableLanguages.firstOrNull() ?: "English"

                                            EditLanguageScreen(
                                                language = Language(
                                                    id = languageId,
                                                    userId = userProfileState!!.id,
                                                    languageName = selectedLanguage,
                                                    languageLevel = "0,0"
                                                ),
                                                onBackClick = { navController.popBackStack() },
                                                onSaveClick = { oral, written ->
                                                    val languageLevel = "$oral,$written"
                                                    profileViewModel.addLanguage(
                                                        selectedLanguage,
                                                        languageLevel
                                                    )
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                    }
                                }

                                composable("edit_appreciations") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        AppreciationScreen(
                                            appreciations = userProfileState!!.appreciations,
                                            onAddClick = { navController.navigate("add_appreciation") },
                                            onBackClick = { navController.popBackStack() },
                                            onRemoveClick = { appreciation ->
                                                profileViewModel.deleteAppreciation(appreciation)
                                            }
                                        )
                                    }
                                }

                                composable("add_appreciation") {
                                    val profileViewModel: ProfileViewModel = viewModel()

                                    AddAppreciationScreen(
                                        onBackClick = { navController.popBackStack() },
                                        onSaveClick = { appreciationText ->
                                            profileViewModel.addAppreciation(appreciationText)
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                composable(
                                    "edit_work_experience/{workExperienceId}",
                                    arguments = listOf(navArgument("workExperienceId") {
                                        type = NavType.LongType
                                    })
                                ) { backStackEntry ->
                                    val workExperienceId =
                                        backStackEntry.arguments?.getLong("workExperienceId") ?: 0L
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        val workExperience =
                                            userProfileState!!.workExperience.firstOrNull { it.id == workExperienceId }
                                        if (workExperience != null) {
                                            EditWorkExperienceScreen(
                                                workExperience = workExperience,
                                                onBackClick = { navController.popBackStack() },
                                                onSaveClick = { updatedWorkExperience ->
                                                    profileViewModel.updateWorkExperience(
                                                        updatedWorkExperience
                                                    )
                                                    navController.popBackStack()
                                                },
                                                onDeleteClick = {
                                                    profileViewModel.deleteWorkExperience(
                                                        workExperienceId
                                                    )
                                                    navController.popBackStack()
                                                }
                                            )
                                        }
                                    }
                                }

                                composable("change_education") {
                                    val profileViewModel: ProfileViewModel = viewModel()
                                    val userProfileState by profileViewModel.userProfile.collectAsState()

                                    if (userProfileState != null) {
                                        ChangeEducationListScreen(
                                            educationList = userProfileState!!.education,
                                            onAddClick = { navController.navigate("add_education") },
                                            onEditClick = { education ->
                                                navController.navigate("edit_education/${education.id}")
                                            },
                                            onBackClick = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }

                                                    // Geometric transition overlay
                            if (animationState.value == AnimationState.ANIMATING) {
                                GeometricTransitionOverlay(
                                    progress = transitionProgress.value,
                                    currentRoute = currentRouteState.value,
                                    previousRoute = previousRoute.value
                                )
                            }
                        }
                    },
                    bottomBar = {
                        if (currentRoute in listOf("home", "savedJobs", "myApplications", "poster", "profile")) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                )
            }
        }
    }

    // Add this function to provide a preview user for testing
    // Update this function to provide a preview user for testing


}

// Animation state enum
enum class AnimationState {
    IDLE, ANIMATING
}

// Custom animated NavHost
@Composable
fun AnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    transitionProgress: Float = 0f,
    animationState: AnimationState = AnimationState.IDLE,
    builder: NavGraphBuilder.() -> Unit,
) {
    // Screen animation properties
    val screenScale by animateFloatAsState(
        targetValue = if (animationState == AnimationState.ANIMATING) 0.9f else 1f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "screenScale"
    )
    val screenAlpha by animateFloatAsState(
        targetValue = if (animationState == AnimationState.ANIMATING) 0.6f else 1f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "screenAlpha"
    )

    Box(modifier = modifier) {
        // Apply animations to the NavHost
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = screenScale
                    scaleY = screenScale
                    alpha = screenAlpha
                },
            builder = builder
        )
    }
}

// Geometric transition overlay with shapes
@Composable
fun GeometricTransitionOverlay(
    progress: Float,
    currentRoute: String?,
    previousRoute: String?
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Determine transition color based on routes
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    // Create geometric shapes
    val shapes = remember {
        List(30) {
            GeometricShape(
                centerX = Random.nextFloat() * screenWidthPx,
                centerY = Random.nextFloat() * screenHeightPx,
                size = Random.nextFloat() * 60f + 20f,
                rotation = Random.nextFloat() * 360f,
                shapeType = ShapeType.values()[Random.nextInt(ShapeType.values().size)],
                color = when (Random.nextInt(3)) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    else -> tertiaryColor
                }
            )
        }
    }

    // Animation effects based on transition progress
    val animatedProgress = remember(progress) { Animatable(0f) }
    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Draw geometric shapes
        Canvas(modifier = Modifier.fillMaxSize()) {
            shapes.forEach { shape ->
                // Calculate current properties based on animation progress
                val currentAlpha = if (progress < 0.5f) {
                    progress * 2
                } else {
                    (1 - progress) * 2
                }
                val currentSize = shape.size * (0.5f + currentAlpha)
                val currentX = shape.centerX
                val currentY = shape.centerY

                // Draw different shapes
                when (shape.shapeType) {
                    ShapeType.CIRCLE -> {
                        drawCircle(
                            color = shape.color.copy(alpha = currentAlpha),
                            radius = currentSize / 2,
                            center = Offset(currentX, currentY)
                        )
                    }
                    ShapeType.SQUARE -> {
                        rotate(shape.rotation + progress * 180, Offset(currentX, currentY)) {
                            drawRect(
                                color = shape.color.copy(alpha = currentAlpha),
                                topLeft = Offset(currentX - currentSize / 2, currentY - currentSize / 2),
                                size = Size(currentSize, currentSize)
                            )
                        }
                    }
                    ShapeType.TRIANGLE -> {
                        val path = Path()
                        rotate(shape.rotation + progress * 180, Offset(currentX, currentY)) {
                            // Create triangle path
                            path.moveTo(currentX, currentY - currentSize / 2)
                            path.lineTo(currentX + currentSize / 2, currentY + currentSize / 2)
                            path.lineTo(currentX - currentSize / 2, currentY + currentSize / 2)
                            path.close()
                            drawPath(
                                path = path,
                                color = shape.color.copy(alpha = currentAlpha)
                            )
                        }
                    }
                    ShapeType.PENTAGON -> {
                        val path = Path()
                        rotate(shape.rotation + progress * 180, Offset(currentX, currentY)) {
                            // Create pentagon path
                            val radius = currentSize / 2
                            val sides = 5
                            for (i in 0 until sides) {
                                val angle = (i * 2 * Math.PI / sides).toFloat()
                                val x = currentX + radius * cos(angle)
                                val y = currentY + radius * sin(angle)
                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            path.close()
                            drawPath(
                                path = path,
                                color = shape.color.copy(alpha = currentAlpha)
                            )
                        }
                    }
                    ShapeType.HEXAGON -> {
                        val path = Path()
                        rotate(shape.rotation + progress * 180, Offset(currentX, currentY)) {
                            // Create hexagon path
                            val radius = currentSize / 2
                            val sides = 6
                            for (i in 0 until sides) {
                                val angle = (i * 2 * Math.PI / sides).toFloat()
                                val x = currentX + radius * cos(angle)
                                val y = currentY + radius * sin(angle)
                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            path.close()
                            drawPath(
                                path = path,
                                color = shape.color.copy(alpha = currentAlpha)
                            )
                        }
                    }
                    ShapeType.STAR -> {
                        val path = Path()
                        rotate(shape.rotation + progress * 180, Offset(currentX, currentY)) {
                            // Create star path
                            val outerRadius = currentSize / 2
                            val innerRadius = outerRadius * 0.4f
                            val points = 5
                            for (i in 0 until points * 2) {
                                val radius = if (i % 2 == 0) outerRadius else innerRadius
                                val angle = (i * Math.PI / points).toFloat()
                                val x = currentX + radius * cos(angle)
                                val y = currentY + radius * sin(angle)
                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            path.close()
                            drawPath(
                                path = path,
                                color = shape.color.copy(alpha = currentAlpha)
                            )
                        }
                    }
                    ShapeType.LINE -> {
                        rotate(shape.rotation + progress * 180, Offset(currentX, currentY)) {
                            drawLine(
                                color = shape.color.copy(alpha = currentAlpha),
                                start = Offset(currentX - currentSize / 2, currentY),
                                end = Offset(currentX + currentSize / 2, currentY),
                                strokeWidth = 4f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            // Draw a central geometric pattern
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxSize = minOf(size.width, size.height) * 0.4f

            // Draw concentric shapes
            for (i in 0 until 3) {
                val shapeSize = maxSize * (1 - i * 0.25f)
                val shapeAlpha = if (progress < 0.5f) {
                    progress * 2 * (1 - i * 0.2f)
                } else {
                    (1 - progress) * 2 * (1 - i * 0.2f)
                }

                // Rotate based on progress
                rotate(progress * 180 * (i + 1), Offset(centerX, centerY)) {
                    when (i % 3) {
                        0 -> { // Hexagon
                            val path = Path()
                            val sides = 6
                            for (j in 0 until sides) {
                                val angle = (j * 2 * Math.PI / sides).toFloat()
                                val x = centerX + shapeSize * cos(angle)
                                val y = centerY + shapeSize * sin(angle)
                                if (j == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            path.close()
                            drawPath(
                                path = path,
                                color = primaryColor.copy(alpha = shapeAlpha),
                                style = Stroke(width = 3f)
                            )
                        }
                        1 -> { // Square
                            drawRect(
                                color = secondaryColor.copy(alpha = shapeAlpha),
                                topLeft = Offset(centerX - shapeSize / 2, centerY - shapeSize / 2),
                                size = Size(shapeSize, shapeSize),
                                style = Stroke(width = 3f)
                            )
                        }
                        2 -> { // Circle
                            drawCircle(
                                color = tertiaryColor.copy(alpha = shapeAlpha),
                                radius = shapeSize / 2,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }
            }
        }

        // Central pulse effect
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val pulseSize by animateFloatAsState(
                targetValue = if (progress < 0.5f) progress * 2 else (1 - progress) * 2,
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                label = "pulseSize"
            )
            Box(
                modifier = Modifier
                    .size(200.dp * pulseSize)
                    .alpha(0.3f * (1 - progress * progress))
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
            )
        }
    }
}

// Geometric shape data class
data class GeometricShape(
    val centerX: Float,
    val centerY: Float,
    val size: Float,
    val rotation: Float,
    val shapeType: ShapeType,
    val color: Color
)

// Shape types enum
enum class ShapeType {
    CIRCLE, SQUARE, TRIANGLE, PENTAGON, HEXAGON, STAR, LINE
}

// Model classes needed for the profile section


@IgnoreExtraProperties
data class UserProfile(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val aboutMe: String = "",
    @get:Exclude
    val workExperience: List<WorkExperience> = emptyList(),
    @get:Exclude
    val education: List<Education> = emptyList(),
    @get:Exclude
    val skills: List<Skill> = emptyList(),
    @get:Exclude
    val languages: List<Language> = emptyList(),
    @get:Exclude
    val location: String = "",
    val resumeFilename: String = "",
    val profileImageUrl: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val appreciations: List<String> = emptyList(),
    var jobTitle : String = ""
    )



@IgnoreExtraProperties
data class WorkExperience(
    val id: Long = 0L,
    val userId: Long = 0L,
    val companyName: String = "",
    val position: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val isCurrentJob: Boolean = false,

    val company: String = "",

    )

@IgnoreExtraProperties
data class Education(
    val id: Long = 0,
    val userId: Long = 0,
    val institution: String = "",
    val degree: String = "",
    val graduationDate: String = "",
    val startDate: String = "" ,
    val endDate: String= "",
    val description: String= "",
)

@IgnoreExtraProperties
data class Skill(
    val id: Long = 0,
    val userId: Long = 0,
    val skillName: String = ""
) {
    // No-arg constructor required for Firestore
    constructor() : this(0, 0, "")
}

@IgnoreExtraProperties
data class Language(
    val id: Long = 0,
    val userId: Long = 0,
    val languageName: String = "",
    val languageLevel: String = "0,0" // Format: "oral,written" where each is a number from 0-5
) {
    // No-arg constructor required for Firestore
    constructor() : this(0, 0, "", "0,0")
}
data class Appreciation(
    val id: String = "",
    val title: String? = null,
    val fromPerson: String? = null,
    val description: String? = null
)
