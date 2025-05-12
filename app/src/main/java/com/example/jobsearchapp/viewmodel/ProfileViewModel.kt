package com.example.jobsearchapp.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobsearchapp.Education
import com.example.jobsearchapp.Language
import com.example.jobsearchapp.Skill
import com.example.jobsearchapp.UserProfile
import com.example.jobsearchapp.WorkExperience
import com.example.jobsearchapp.ui.profile.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    // Firebase instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // State flows
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _workExperiences = MutableStateFlow<List<WorkExperience>>(emptyList())
    val workExperiences: StateFlow<List<WorkExperience>> = _workExperiences

    init {
        loadUserProfile()
        setupRealtimeListeners()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _error.value = "User not authenticated"
                    _isLoading.value = false
                    return@launch
                }

                val userId = currentUser.uid

                // Fetch basic profile data
                val profileDoc = db.collection("profiles").document(userId).get().await()

                if (!profileDoc.exists()) {
                    // Create a new profile if it doesn't exist
                    createNewUserProfile(userId)
                    return@launch
                }

                // Fetch work experiences
                val workExperiencesSnapshot = db.collection("profiles")
                    .document(userId)
                    .collection("workExperiences")
                    .get()
                    .await()

                val workExperiences = workExperiencesSnapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(WorkExperience::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error parsing work experience: ${e.message}")
                        null
                    }
                }

                // Fetch education
                val educationSnapshot = db.collection("profiles")
                    .document(userId)
                    .collection("education")
                    .get()
                    .await()

                val education = educationSnapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Education::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error parsing education: ${e.message}")
                        null
                    }
                }

                // Fetch skills
                val skillsSnapshot = db.collection("profiles")
                    .document(userId)
                    .collection("skills")
                    .get()
                    .await()

                val skills = skillsSnapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Skill::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error parsing skill: ${e.message}")
                        null
                    }
                }

                // Fetch languages
                val languagesSnapshot = db.collection("profiles")
                    .document(userId)
                    .collection("languages")
                    .get()
                    .await()

                val languages = languagesSnapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Language::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error parsing language: ${e.message}")
                        null
                    }
                }

                // Fetch appreciations
                val appreciationsSnapshot = db.collection("profiles")
                    .document(userId)
                    .collection("appreciations")
                    .get()
                    .await()

                val appreciations = appreciationsSnapshot.documents.mapNotNull { doc ->
                    doc.getString("text")
                }

                // Get name parts
                val fullName = profileDoc.getString("name") ?: currentUser.displayName ?: ""
                val nameParts = fullName.split(" ", limit = 2)
                val firstName = nameParts.getOrNull(0) ?: ""
                val lastName = nameParts.getOrElse(1) { "" }

                // Construct the full profile
                val profile = UserProfile(
                    id = userId.hashCode().toLong(),
                    name = fullName,
                    firstName = firstName,
                    lastName = lastName,
                    email = profileDoc.getString("email") ?: currentUser.email ?: "",
                    phone = profileDoc.getString("phone") ?: "",
                    profileImageUrl = profileDoc.getString("profileImageUrl"),
                    jobTitle = profileDoc.getString("jobTitle") ?: "Professional",
                    aboutMe = profileDoc.getString("aboutMe") ?: "",
                    workExperience = workExperiences,
                    education = education,
                    skills = skills,
                    languages = languages,
                    location = profileDoc.getString("location") ?: "",
                    resumeFilename = profileDoc.getString("resumeFilename") ?: "",
                    appreciations = appreciations
                )

                _userProfile.value = profile
                _workExperiences.value = workExperiences

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile: ${e.message}", e)
                _error.value = "Error loading profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createNewUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch

                // Get name parts
                val fullName = currentUser.displayName ?: ""
                val nameParts = fullName.split(" ", limit = 2)
                val firstName = nameParts.getOrNull(0) ?: ""
                val lastName = nameParts.getOrElse(1) { "" }

                // Create basic profile
                val profileData = hashMapOf(
                    "name" to fullName,
                    "email" to (currentUser.email ?: ""),
                    "phone" to "",
                    "aboutMe" to "",
                    "location" to "",
                    "resumeFilename" to "",
                    "jobTitle" to "Professional",
                    "profileImageUrl" to (currentUser.photoUrl?.toString() ?: "")
                )

                db.collection("profiles").document(userId).set(profileData).await()

                // Create the profile object
                val profile = UserProfile(
                    id = userId.hashCode().toLong(),
                    name = fullName,
                    firstName = firstName,
                    lastName = lastName,
                    email = currentUser.email ?: "",
                    phone = "",
                    aboutMe = "",
                    workExperience = emptyList(),
                    education = emptyList(),
                    skills = emptyList(),
                    languages = emptyList(),
                    jobTitle = "Professional",
                    profileImageUrl = currentUser.photoUrl?.toString(),
                    location = "",
                    resumeFilename = "",
                    appreciations = emptyList()
                )

                _userProfile.value = profile

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error creating profile: ${e.message}", e)
                _error.value = "Error creating profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateAboutMe(newAboutText: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles").document(userId)
                    .update("aboutMe", newAboutText)
                    .await()

                // Update local state
                _userProfile.value = _userProfile.value?.copy(aboutMe = newAboutText)
            } catch (e: Exception) {
                _error.value = "Error updating about me: ${e.message}"
            }
        }
    }

    // Add work experience
    fun addWorkExperience(workExperience: WorkExperience) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                // Create a document with auto-generated ID
                val docRef = db.collection("profiles")
                    .document(userId)
                    .collection("workExperiences")
                    .document()

                // Use the document ID as the work experience ID
                val newWorkExperience = workExperience.copy(
                    id = System.currentTimeMillis(),
                    userId = userId.hashCode().toLong()
                )

                docRef.set(newWorkExperience).await()

                // Update local state
                val currentWorkExperiences = _userProfile.value?.workExperience ?: emptyList()
                _userProfile.value = _userProfile.value?.copy(
                    workExperience = (currentWorkExperiences + newWorkExperience) as List<WorkExperience>
                )
            } catch (e: Exception) {
                _error.value = "Error adding work experience: ${e.message}"
            }
        }
    }

    // Update work experience
    fun updateWorkExperience(updatedWorkExperience: WorkExperience) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles")
                    .document(userId)
                    .collection("workExperiences")
                    .document(updatedWorkExperience.id.toString())
                    .set(updatedWorkExperience)
                    .await()

                // Update local state
                val currentWorkExperiences = _userProfile.value?.workExperience ?: emptyList()
                val updatedList = currentWorkExperiences.map {
                    if (it.id == updatedWorkExperience.id) updatedWorkExperience else it
                }
                _userProfile.value = _userProfile.value?.copy(workExperience = updatedList)
            } catch (e: Exception) {
                _error.value = "Error updating work experience: ${e.message}"
            }
        }
    }
    // Function to upload profile image
    fun uploadProfileImage(imageUri: Uri, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                // Create a reference to the storage location
                val storageRef = storage.reference.child("profile_images/$userId.jpg")

                // Upload the file
                storageRef.putFile(imageUri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        storageRef.downloadUrl
                    }
                    .addOnCompleteListener { task ->
                        _isLoading.value = false
                        if (task.isSuccessful) {
                            val downloadUri = task.result

                            // Update Firestore with the new image URL
                            db.collection("profiles").document(userId)
                                .update("profileImageUrl", downloadUri.toString())
                                .addOnSuccessListener {
                                    // Update local state
                                    val currentProfile = _userProfile.value
                                    if (currentProfile != null) {
                                        _userProfile.value = currentProfile.copy(
                                            profileImageUrl = downloadUri.toString()
                                        )
                                    }
                                    onSuccess(downloadUri.toString())
                                }
                                .addOnFailureListener { e ->
                                    _error.value = "Failed to update profile image: ${e.message}"
                                }
                        } else {
                            _error.value = "Failed to upload image: ${task.exception?.message}"
                        }
                    }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error uploading image: ${e.message}"
            }
        }
    }

    // Other methods remain the same...
    // (I'm omitting them for brevity, but they should be kept in your actual code)

    private fun setupRealtimeListeners() {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        // Listen for profile document changes
        db.collection("profiles").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error listening to profile changes: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Update basic profile data
                    val currentProfile = _userProfile.value
                    if (currentProfile != null) {
                        val fullName = snapshot.getString("name") ?: currentProfile.name
                        val nameParts = fullName.split(" ", limit = 2)
                        val firstName = nameParts.getOrNull(0) ?: ""
                        val lastName = nameParts.getOrElse(1) { "" }

                        _userProfile.value = currentProfile.copy(
                            name = fullName,
                            firstName = firstName,
                            lastName = lastName,
                            email = snapshot.getString("email") ?: currentProfile.email,
                            phone = snapshot.getString("phone") ?: currentProfile.phone,
                            aboutMe = snapshot.getString("aboutMe") ?: currentProfile.aboutMe,
                            location = snapshot.getString("location") ?: currentProfile.location,
                            resumeFilename = snapshot.getString("resumeFilename") ?: currentProfile.resumeFilename,
                            profileImageUrl = snapshot.getString("profileImageUrl") ?: currentProfile.profileImageUrl,
                            jobTitle = snapshot.getString("jobTitle") ?: currentProfile.jobTitle
                        )
                    }
                }
            }



        // Listen for work experiences changes
        db.collection("profiles").document(userId).collection("workExperiences")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error listening to work experiences: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val workExperiences = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(WorkExperience::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    }

                    _userProfile.value = _userProfile.value?.copy(workExperience = workExperiences)
                }
            }

        // Listen for education changes
        db.collection("profiles").document(userId).collection("education")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error listening to education changes: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val education = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Education::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    }

                    _userProfile.value = _userProfile.value?.copy(education = education)
                }
            }

        // Listen for skills changes
        db.collection("profiles").document(userId).collection("skills")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error listening to skills changes: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val skills = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Skill::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    }

                    _userProfile.value = _userProfile.value?.copy(skills = skills)
                }
            }

        // Listen for languages changes
        db.collection("profiles").document(userId).collection("languages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error listening to languages changes: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val languages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Language::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    }

                    _userProfile.value = _userProfile.value?.copy(languages = languages)
                }
            }

        // Listen for appreciations changes
        db.collection("profiles").document(userId).collection("appreciations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _error.value = "Error listening to appreciations changes: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val appreciations = snapshot.documents.mapNotNull { doc ->
                        doc.getString("text")
                    }

                    _userProfile.value = _userProfile.value?.copy(appreciations = appreciations)
                }
            }
    }

    // Call this method in your init block
    init {
        loadUserProfile()
        setupRealtimeListeners()
    }

    // Delete work experience
    fun deleteWorkExperience(workExperienceId: Long) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles")
                    .document(userId)
                    .collection("workExperiences")
                    .document(workExperienceId.toString())
                    .delete()
                    .await()

                // Update local state
                val currentWorkExperiences = _userProfile.value?.workExperience ?: emptyList()
                val updatedList = currentWorkExperiences.filter { it.id != workExperienceId }
                _userProfile.value = _userProfile.value?.copy(workExperience = updatedList)
            } catch (e: Exception) {
                _error.value = "Error deleting work experience: ${e.message}"
            }
        }
    }

    // Add education
    fun addEducation(institution: String, degree: String, graduationDate: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val newEducation = Education(
                    id = System.currentTimeMillis(),
                    userId = userId.hashCode().toLong(),
                    institution = institution,
                    degree = degree,
                    graduationDate = graduationDate,
                )

                db.collection("profiles")
                    .document(userId)
                    .collection("education")
                    .document(newEducation.id.toString())
                    .set(newEducation)
                    .await()

                // Update local state
                val currentEducation = _userProfile.value?.education ?: emptyList()
                _userProfile.value = _userProfile.value?.copy(
                    education = currentEducation + newEducation
                )
            } catch (e: Exception) {
                _error.value = "Error adding education: ${e.message}"
            }
        }
    }

    // Update education
    fun updateEducation(educationId: Long, institution: String, degree: String, graduationDate: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val updatedEducation = Education(
                    id = educationId,
                    userId = userId.hashCode().toLong(),
                    institution = institution,
                    degree = degree,
                    graduationDate = graduationDate,
                )

                db.collection("profiles")
                    .document(userId)
                    .collection("education")
                    .document(educationId.toString())
                    .set(updatedEducation)
                    .await()

                // Update local state
                val currentEducation = _userProfile.value?.education ?: emptyList()
                val updatedList = currentEducation.map {
                    if (it.id == educationId) updatedEducation else it
                }
                _userProfile.value = _userProfile.value?.copy(education = updatedList)
            } catch (e: Exception) {
                _error.value = "Error updating education: ${e.message}"
            }
        }
    }

    // Delete education
    fun deleteEducation(educationId: Long) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles")
                    .document(userId)
                    .collection("education")
                    .document(educationId.toString())
                    .delete()
                    .await()

                // Update local state
                val currentEducation = _userProfile.value?.education ?: emptyList()
                val updatedList = currentEducation.filter { it.id != educationId }
                _userProfile.value = _userProfile.value?.copy(education = updatedList)
            } catch (e: Exception) {
                _error.value = "Error deleting education: ${e.message}"
            }
        }
    }

    // Add skill
    fun addSkill(skillName: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val newSkill = Skill(
                    id = System.currentTimeMillis(),
                    userId = userId.hashCode().toLong(),
                    skillName = skillName
                )

                db.collection("profiles")
                    .document(userId)
                    .collection("skills")
                    .document(newSkill.id.toString())
                    .set(newSkill)
                    .await()

                // Update local state
                val currentSkills = _userProfile.value?.skills ?: emptyList()
                _userProfile.value = _userProfile.value?.copy(
                    skills = currentSkills + newSkill
                )
            } catch (e: Exception) {
                _error.value = "Error adding skill: ${e.message}"
            }
        }
    }

    // Delete skill
    fun deleteSkill(skillId: Long) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles")
                    .document(userId)
                    .collection("skills")
                    .document(skillId.toString())
                    .delete()
                    .await()

                // Update local state
                val currentSkills = _userProfile.value?.skills ?: emptyList()
                val updatedList = currentSkills.filter { it.id != skillId }
                _userProfile.value = _userProfile.value?.copy(skills = updatedList)
            } catch (e: Exception) {
                _error.value = "Error deleting skill: ${e.message}"
            }
        }
    }

    // Add language
    fun addLanguage(languageName: String, languageLevel: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val newLanguage = Language(
                    id = System.currentTimeMillis(),
                    userId = userId.hashCode().toLong(),
                    languageName = languageName,
                    languageLevel = languageLevel
                )

                db.collection("profiles")
                    .document(userId)
                    .collection("languages")
                    .document(newLanguage.id.toString())
                    .set(newLanguage)
                    .await()

                // Update local state
                val currentLanguages = _userProfile.value?.languages ?: emptyList()
                _userProfile.value = _userProfile.value?.copy(
                    languages = currentLanguages + newLanguage
                )
            } catch (e: Exception) {
                _error.value = "Error adding language: ${e.message}"
            }
        }
    }

    // Update language
    fun updateLanguage(languageId: Long, languageName: String, languageLevel: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val updatedLanguage = Language(
                    id = languageId,
                    userId = userId.hashCode().toLong(),
                    languageName = languageName,
                    languageLevel = languageLevel
                )

                db.collection("profiles")
                    .document(userId)
                    .collection("languages")
                    .document(languageId.toString())
                    .set(updatedLanguage)
                    .await()

                // Update local state
                val currentLanguages = _userProfile.value?.languages ?: emptyList()
                val updatedList = currentLanguages.map {
                    if (it.id == languageId) updatedLanguage else it
                }
                _userProfile.value = _userProfile.value?.copy(languages = updatedList)
            } catch (e: Exception) {
                _error.value = "Error updating language: ${e.message}"
            }
        }
    }

    // Delete language
    fun deleteLanguage(languageId: Long) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles")
                    .document(userId)
                    .collection("languages")
                    .document(languageId.toString())
                    .delete()
                    .await()

                // Update local state
                val currentLanguages = _userProfile.value?.languages ?: emptyList()
                val updatedList = currentLanguages.filter { it.id != languageId }
                _userProfile.value = _userProfile.value?.copy(languages = updatedList)
            } catch (e: Exception) {
                _error.value = "Error deleting language: ${e.message}"
            }
        }
    }

    // Add appreciation
    fun addAppreciation(appreciationText: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val appreciationId = System.currentTimeMillis().toString()
                val appreciationData = hashMapOf("text" to appreciationText)

                db.collection("profiles")
                    .document(userId)
                    .collection("appreciations")
                    .document(appreciationId)
                    .set(appreciationData)
                    .await()

                // Update local state
                val currentAppreciations = _userProfile.value?.appreciations ?: emptyList()
                _userProfile.value = _userProfile.value?.copy(
                    appreciations = currentAppreciations + appreciationText
                )
            } catch (e: Exception) {
                _error.value = "Error adding appreciation: ${e.message}"
            }
        }
    }

    // Delete appreciation
    fun deleteAppreciation(appreciationText: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                // Find the document with this text
                val appreciationsSnapshot = db.collection("profiles")
                    .document(userId)
                    .collection("appreciations")
                    .whereEqualTo("text", appreciationText)
                    .get()
                    .await()

                // Delete the found document
                if (!appreciationsSnapshot.isEmpty) {
                    val docId = appreciationsSnapshot.documents[0].id
                    db.collection("profiles")
                        .document(userId)
                        .collection("appreciations")
                        .document(docId)
                        .delete()
                        .await()
                }

                // Update local state
                val currentAppreciations = _userProfile.value?.appreciations ?: emptyList()
                val updatedList = currentAppreciations.filter { it != appreciationText }
                _userProfile.value = _userProfile.value?.copy(appreciations = updatedList)
            } catch (e: Exception) {
                _error.value = "Error deleting appreciation: ${e.message}"
            }
        }
    }

    // Update profile basic info
    fun updateProfileBasicInfo(name: String, phone: String, location: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                val updates = hashMapOf<String, Any>(
                    "name" to name,
                    "phone" to phone,
                    "location" to location
                )

                db.collection("profiles")
                    .document(userId)
                    .update(updates)
                    .await()

                // Update local state
                _userProfile.value = _userProfile.value?.copy(
                    name = name,
                    phone = phone,
                    location = location
                )
            } catch (e: Exception) {
                _error.value = "Error updating profile info: ${e.message}"
            }
        }
    }

    // Update resume filename
    fun updateResumeFilename(filename: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userId = currentUser.uid

                db.collection("profiles")
                    .document(userId)
                    .update("resumeFilename", filename)
                    .await()

                // Update local state
                _userProfile.value = _userProfile.value?.copy(resumeFilename = filename)
            } catch (e: Exception) {
                _error.value = "Error updating resume filename: ${e.message}"
            }
        }
    }

    // Clear error message
    fun clearError() {
        _error.value = null
    }
}

