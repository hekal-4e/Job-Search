package com.example.jobsearchapp.ui.profile

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.example.jobsearchapp.ui.profile.Appreciation
import com.example.jobsearchapp.ui.profile.Education
import com.example.jobsearchapp.ui.profile.Language
import com.example.jobsearchapp.ui.profile.Skill
import com.example.jobsearchapp.ui.profile.WorkExperience

@IgnoreExtraProperties
data class UserProfile(
    val id: Long,
    val name: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val jobTitle: String? = null,
    val location: String = "",
    val aboutMe: String? = null,
    val workExperience: List<WorkExperience> = emptyList(),
    val education: List<Education> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val languages: List<Language> = emptyList(),
    val appreciations: List<String>? = null,
    val resumeFilename: String? = null,

    val phoneNumber: String? = null,


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


// Work Experience model for UI
data class WorkExperience(
    val id: String? = null,
    val position: String? = null,
    val userId: Long? = null,

    val jobTitle: String? = null,
    val company: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val description: String? = null
)

// Education model for UI
data class Education(
    val id: String? = null,
    val degree: String? = null,
    val institution: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val description: String? = null
)

// Appreciation model for UI
data class Appreciation(
    val id: String? = null,
    val title: String? = null,
    val fromPerson: String? = null,
    val description: String? = null
)
