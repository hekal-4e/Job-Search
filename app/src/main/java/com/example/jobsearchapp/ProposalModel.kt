package com.example.jobsearchapp

import com.example.jobsearchapp.notifications.ApplicationStatus
import com.google.firebase.Timestamp




data class Proposal(
    val id: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val applicantId: String = "",
    val applicantName: String = "",
    val applicantEmail: String = "",
    val applicantPhone: String = "",
    val coverLetter: String = "",
    val cvUrl: String = "",
    val portfolioUrl: String = "",
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val timestamp: Timestamp? = null,
    val recruiterNotes: String = ""
)

