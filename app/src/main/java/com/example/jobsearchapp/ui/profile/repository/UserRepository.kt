package com.example.jobsearchapp.ui.profile.repository

import com.example.jobsearchapp.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    // Update the user profile
    suspend fun updateUserProfile(userProfile: UserProfile) {
        val userId = currentUserId

        withContext(Dispatchers.IO) {
            // Update basic user data
            val userData = mapOf(
                "id" to userProfile.id,
                "name" to userProfile.name,
                "email" to userProfile.email,
                "aboutMe" to userProfile.aboutMe,
                "resumeFilename" to userProfile.resumeFilename,
                "appreciations" to userProfile.appreciations,
                "location" to userProfile.location,
                "phone" to userProfile.phone
            )

            firestore.collection("profiles").document(userId).set(userData).await()

            // Update work experiences
            userProfile.workExperience?.let { experiences ->
                // Delete existing work experiences
                val existingExperiences = firestore.collection("profiles")
                    .document(userId)
                    .collection("workExperiences")
                    .get()
                    .await()

                // Delete each document
                for (doc in existingExperiences) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("workExperiences")
                        .document(doc.id)
                        .delete()
                        .await()
                }

                // Add new work experiences
                for (experience in experiences) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("workExperiences")
                        .document(experience.id.toString())
                        .set(experience)
                        .await()
                }
            }

            // Update education
            userProfile.education?.let { educations ->
                // Delete existing education entries
                val existingEducation = firestore.collection("profiles")
                    .document(userId)
                    .collection("education")
                    .get()
                    .await()

                // Delete each document
                for (doc in existingEducation) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("education")
                        .document(doc.id)
                        .delete()
                        .await()
                }

                // Add new education entries
                for (education in educations) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("education")
                        .document(education.id.toString())
                        .set(education)
                        .await()
                }
            }

            // Update skills
            userProfile.skills?.let { skills ->
                // Delete existing skills
                val existingSkills = firestore.collection("profiles")
                    .document(userId)
                    .collection("skills")
                    .get()
                    .await()

                // Delete each document
                for (doc in existingSkills) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("skills")
                        .document(doc.id)
                        .delete()
                        .await()
                }

                // Add new skills
                for (skill in skills) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("skills")
                        .document(skill.id.toString())
                        .set(skill)
                        .await()
                }
            }

            // Update languages
            userProfile.languages?.let { languages ->
                // Delete existing languages
                val existingLanguages = firestore.collection("profiles")
                    .document(userId)
                    .collection("languages")
                    .get()
                    .await()

                // Delete each document
                for (doc in existingLanguages) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("languages")
                        .document(doc.id)
                        .delete()
                        .await()
                }

                // Add new languages
                for (language in languages) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("languages")
                        .document(language.id.toString())
                        .set(language)
                        .await()
                }
            }

            // Update appreciations
            userProfile.appreciations?.let { appreciations ->
                // Delete existing appreciations
                val existingAppreciations = firestore.collection("profiles")
                    .document(userId)
                    .collection("appreciations")
                    .get()
                    .await()

                // Delete each document
                for (doc in existingAppreciations) {
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("appreciations")
                        .document(doc.id)
                        .delete()
                        .await()
                }

                // Add new appreciations
                for (appreciation in appreciations) {
                    val appreciationData = hashMapOf("text" to appreciation)
                    firestore.collection("profiles")
                        .document(userId)
                        .collection("appreciations")
                        .document()
                        .set(appreciationData)
                        .await()
                }
            }
        }
    }
}
