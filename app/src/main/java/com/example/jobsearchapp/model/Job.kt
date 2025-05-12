package com.example.jobsearchapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Job(
    var id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val description: String = "",
    val requirements: String = "",
    val salary: String = "",
    val type: String = "",
    val category: String = "",
    val uid: String = "",
    val posterEmail: String = "",
    val cvUrl: String? = null,
    val deadline: Boolean = false,
    val companyLogo: String = "",
    val skills: List<String> = listOf(),

    @ServerTimestamp
    val timestamp: Timestamp? = null
) {
    // Empty constructor required for Firestore
    constructor() : this(
        id = "",
        title = "",
        company = "",
        location = "",
        description = "",
        requirements = "",
        salary = "",
        type = "",
        category = "",
        uid = "",
        posterEmail = "",
        cvUrl = null,
        deadline = false,
        companyLogo = "",
        skills = listOf(),
        timestamp = null
    )
}
