package com.example.jobsearchapp

import android.util.Log
import com.example.jobsearchapp.model.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class JobRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getJobs(): Result<List<Job>> {
        return try {
            val jobs = db.collection("jobs")
                .get()
                .await()
                .toObjects(Job::class.java)
                .sortedByDescending(Job::timestamp)

            Result.success(jobs)
        } catch (e: Exception) {
            Log.e("JobRepository", "Error getting jobs", e)
            Result.failure(e)
        }
    }

    suspend fun addJob(job: Job): Result<Job> {
        return try {
            val currentUser = auth.currentUser ?: return Result.failure(Exception("User not logged in"))

            val jobId = UUID.randomUUID().toString()
            val newJob = job.copy(
                id = jobId,
                uid = currentUser.uid,
                posterEmail = currentUser.email ?: ""
            )

            db.collection("jobs").document(jobId).set(newJob).await()

            Result.success(newJob)
        } catch (e: Exception) {
            Log.e("JobRepository", "Error adding job", e)
            Result.failure(e)
        }
    }

    suspend fun saveJob(userId: String, job: Job): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("savedJobs")
                .document(job.id)
                .set(job)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JobRepository", "Error saving job", e)
            Result.failure(e)
        }
    }

    suspend fun removeSavedJob(userId: String, jobId: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("savedJobs")
                .document(jobId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JobRepository", "Error removing saved job", e)
            Result.failure(e)
        }
    }

    suspend fun getSavedJobs(userId: String): Result<List<Job>> {
        return try {
            val jobs = db.collection("users")
                .document(userId)
                .collection("savedJobs")
                .get()
                .await()
                .toObjects(Job::class.java)

            Result.success(jobs)
        } catch (e: Exception) {
            Log.e("JobRepository", "Error getting saved jobs", e)
            Result.failure(e)
        }
    }
}
