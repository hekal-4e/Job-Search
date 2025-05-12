package com.example.jobsearchapp.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val bio: String = "",
    val skills: List<String> = listOf(),
    val experience: String = "",
    val education: String = "",
    val location: String = "",
    val profilePictureUrl: String = "",
    val resumeUrl: String = "",
    val isRecruiter: Boolean = false,
    val company: String = "",
    val companyDescription: String = "",
    val companyWebsite: String = "",
    val companyLogo: String = ""
)
