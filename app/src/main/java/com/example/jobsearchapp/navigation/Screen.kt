package com.example.jobsearchapp.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object JobDetails : Screen("jobDetails/{jobId}") {
        fun passJobId(jobId: String) = "jobDetails/$jobId"
    }
    object Search : Screen("search")
    object Profile : Screen("profile")
    object SavedJobs : Screen("savedJobs")
}

