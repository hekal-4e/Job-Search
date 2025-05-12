package com.example.jobsearchapp

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.jobsearchapp.model.Job
import com.example.jobsearchapp.notifications.ApplicationStatus
import com.example.jobsearchapp.notifications.EmailService
import com.example.jobsearchapp.notifications.NotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ProposalRepository(
    private val context: Context,
    private val notificationService: NotificationService
) {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val emailService = EmailService(context)

    suspend fun submitProposal(job: Job, proposalText: String, cvUri: Uri?): Result<Proposal> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("User not logged in"))

            // Upload CV to Firebase Storage if provided
            val cvUrl = cvUri?.let { uploadCv(it) } ?: ""

            // Create proposal object
            val proposal = Proposal(
                id = UUID.randomUUID().toString(),
                jobId = job.id,
                jobTitle = job.title,
                applicantId = currentUser.uid,
                applicantEmail = currentUser.email ?: "",
                coverLetter = proposalText,
                cvUrl = cvUrl,
                status = ApplicationStatus.SENT,
            )

            // Save proposal to Firestore
            db.collection("proposals").document(proposal.id).set(proposal).await()

            // Send notification to the job poster
            val posterProposal = proposal.copy(status = ApplicationStatus.NEW_PROPOSAL)
            notificationService.showJobNotification(
                posterProposal
            )

            // Send notification to the applicant
            notificationService.showJobNotification(
                proposal
            )

            // Send email notifications
            emailService.sendApplicationConfirmationEmail(proposal, job)
            emailService.sendNewApplicationNotificationEmail(proposal, job)

            Result.success(proposal)
        } catch (e: Exception) {
            Log.e("ProposalRepository", "Error submitting proposal", e)
            Result.failure(e)
        }
    }

    private suspend fun uploadCv(cvUri: Uri): String {
        val ref = storage.reference.child("cvs/${UUID.randomUUID()}")
        return ref.putFile(cvUri).await().storage.downloadUrl.await().toString()
    }

    suspend fun updateProposalStatus(proposalId: String, newStatus: ApplicationStatus): Result<Proposal> {
        return try {
            // Get the proposal
            val proposalDoc = db.collection("proposals").document(proposalId).get().await()
            val proposal = proposalDoc.toObject(Proposal::class.java)
                ?: return Result.failure(Exception("Proposal not found"))

            // Get the job
            val jobDoc = db.collection("jobs").document(proposal.jobId).get().await()
            val job = jobDoc.toObject(Job::class.java)
                ?: return Result.failure(Exception("Job not found"))

            // Update status
            val updatedProposal = proposal.copy(status = newStatus)
            db.collection("proposals").document(proposalId).set(updatedProposal).await()

            // Send notification to the applicant
            notificationService.showJobNotification(
                updatedProposal
            )

            // Send email notification
            emailService.sendApplicationStatusUpdateEmail(updatedProposal, job)

            Result.success(updatedProposal)
        } catch (e: Exception) {
            Log.e("ProposalRepository", "Error updating proposal status", e)
            Result.failure(e)
        }
    }

    suspend fun getProposalsForJob(jobId: String): Result<List<Proposal>> {
        return try {
            val proposals = db.collection("proposals")
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
                .toObjects(Proposal::class.java)

            Result.success(proposals)
        } catch (e: Exception) {
            Log.e("ProposalRepository", "Error getting proposals for job", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProposals(userId: String): Result<List<Proposal>> {
        return try {
            val proposals = db.collection("proposals")
                .whereEqualTo("applicantId", userId)
                .get()
                .await()
                .toObjects(Proposal::class.java)
                .sortedByDescending { it.timestamp }

            Result.success(proposals)
        } catch (e: Exception) {
            Log.e("ProposalRepository", "Error getting user proposals", e)
            Result.failure(e)
        }
    }}