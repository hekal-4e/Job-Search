package com.example.jobsearchapp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

import android.util.Log
import com.example.jobsearchapp.model.Job
import com.example.jobsearchapp.notifications.ApplicationStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobsearchapp.model.NotificationItem
import com.example.jobsearchapp.model.Chat
import com.example.jobsearchapp.model.Message
import com.example.jobsearchapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class AppViewModel : ViewModel() {
    private var messagesListener: ListenerRegistration? = null

    // Jobs
    private val _jobs = mutableStateListOf<Job>()
    val jobs: List<Job> = _jobs
    val firestore = Firebase.firestore

    // Saved jobs
    private val _savedJobs = mutableStateListOf<Job>()
    val savedJobs: List<Job> = _savedJobs

    // Job proposals (applications)
    private val _jobProposals = mutableStateListOf<Proposal>()
    val jobProposals: List<Proposal> = _jobProposals
    val isLoading = mutableStateOf(false)

    // MutableState for error messages
    val error = mutableStateOf<String?>(null)


    // MutableState for current user profile
    val currentUserProfile = mutableStateOf<User?>(null)

    // Messages listener
    // User proposals (applications)
    private val _userProposals = mutableStateListOf<Proposal>()
    val userProposals: List<Proposal> = _userProposals

    // Notifications
    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> get() = _notifications

    // Unread notifications count
    var unreadNotificationsCount by mutableStateOf(0)
        private set

    // Featured jobs for home screen
    private val _featuredJobs = mutableStateListOf<Job>()
    val featuredJobs: List<Job> = _featuredJobs
    private val _chats = mutableStateListOf<Chat>()
    val chats: List<Chat> = _chats

    // Messages for current chat
    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    // Current chat ID
    private val _currentChatId = mutableStateOf<String?>(null)
    val currentChatId: String? get() = _currentChatId.value

    // Load all jobs
    fun loadJobs() {
        val db = FirebaseFirestore.getInstance()
        db.collection("jobs")
            .get()
            .addOnSuccessListener { documents ->
                _jobs.clear()
                _featuredJobs.clear()
                for (document in documents) {
                    val job = document.toObject(Job::class.java)
                    _jobs.add(job)
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error loading jobs", e)
            }
    }

    // Check if a job is saved by the current user
    fun isJobSaved(jobId: String): Boolean {
        return savedJobs.any { it.id == jobId }
    }

    // Remove a saved job
    fun removeSavedJob(jobId: String) {
        val job = savedJobs.find { it.id == jobId } ?: return
        unsaveJob(job)
    }

    // Load saved jobs for current user
    fun loadSavedJobs() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("savedJobs")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                _savedJobs.clear()
                val jobIds = documents.map { it.getString("jobId") ?: "" }
                // Get the full job details for each saved job ID
                for (jobId in jobIds) {
                    val job = _jobs.find { it.id == jobId }
                    if (job != null) {
                        _savedJobs.add(job)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error loading saved jobs", e)
            }
    }

    // Load proposals for a specific job
    fun loadJobProposals(jobId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("proposals")
            .whereEqualTo("jobId", jobId)
            .get()
            .addOnSuccessListener { documents ->
                val proposals = documents.mapNotNull { it.toObject(Proposal::class.java) }
                _jobProposals.clear()
                _jobProposals.addAll(proposals)
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error loading job proposals", e)
            }
    }

    // Load proposals for current user
    fun loadUserProposals() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("proposals")
            .whereEqualTo("applicantId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                val proposals = documents.mapNotNull { it.toObject(Proposal::class.java) }
                _userProposals.clear()
                _userProposals.addAll(proposals)
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error loading user proposals", e)
            }
    }

    // Add a new job
    fun addJob(
        title: String,
        company: String,
        location: String,
        description: String,
        requirements: String,
        salary: String,
        type: String,
        category: String,
        featured: Boolean = false
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val job = Job(
            id = UUID.randomUUID().toString(),
            title = title,
            company = company,
            location = location,
            description = description,
            requirements = requirements,
            salary = salary,
            type = type,
            category = category,
            uid = currentUser.uid,
            posterEmail = currentUser.email ?: "",
            timestamp = Timestamp.now(),
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("jobs").document(job.id)
            .set(job)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Job added successfully")
                loadJobs() // Refresh the jobs list
                // Create notification for job poster
                createNotification(
                    title = "Job Posted Successfully",
                    message = "Your job listing for \"$title\" has been posted successfully.",
                    type = "job_posted",
                    relatedId = job.id
                )
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error adding job", e)
            }
    }

    // Save a job for the current user
    fun saveJob(job: Job) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val savedJob = hashMapOf(
            "userId" to currentUser.uid,
            "jobId" to job.id,
            "timestamp" to System.currentTimeMillis()
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("savedJobs").document("${currentUser.uid}_${job.id}")
            .set(savedJob)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Job saved successfully")
                _savedJobs.add(job)
                // Create notification for saved job
                createNotification(
                    title = "Job Saved",
                    message = "You saved \"${job.title}\" at ${job.company} to your favorites.",
                    type = "job_saved",
                    relatedId = job.id
                )
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error saving job", e)
            }
    }

    // Unsave a job for the current user
    fun unsaveJob(job: Job) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("savedJobs").document("${currentUser.uid}_${job.id}")
            .delete()
            .addOnSuccessListener {
                Log.d("AppViewModel", "Job unsaved successfully")
                _savedJobs.removeIf { it.id == job.id }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error unsaving job", e)
            }
    }

    // Create a notification for the current user
    fun createNotification(
        title: String,
        message: String,
        type: String = "general",
        relatedId: String? = null
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val notification = relatedId?.let {
            NotificationItem(
                id = UUID.randomUUID().toString(),
                title = title,
                message = message,
                userId = currentUser.uid,
                type = type,
                relatedId = it,
                timestamp = Timestamp.now(),
                read = false
            )
        }
        if (notification != null) {
            saveNotificationToFirestore(notification)
        }
    }

    // Save notification to Firestore
    private fun saveNotificationToFirestore(notification: NotificationItem) {
        val db = FirebaseFirestore.getInstance()
        val notificationData = hashMapOf(
            "userId" to notification.userId,
            "title" to notification.title,
            "message" to notification.message,
            "timestamp" to notification.timestamp,
            "read" to notification.read,
            "type" to notification.type,
            "relatedId" to notification.relatedId
        )
        // Use the notification's ID if it exists, otherwise let Firestore generate one
        val documentId =
            if (notification.id.isNotEmpty()) notification.id else UUID.randomUUID().toString()
        db.collection("notifications").document(documentId)
            .set(notificationData)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Notification created with ID: $documentId")
                // If this is a notification for the current user, add it to the local list
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null && notification.userId == currentUser.uid) {
                    // Create a copy with the document ID
                    val newNotification = notification.copy(id = documentId)
                    // Add to the beginning of the list for newest first
                    _notifications.add(0, newNotification)
                    updateUnreadNotificationsCount()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error creating notification", e)
            }
    }

    // Update unread notifications count
    private fun updateUnreadNotificationsCount() {
        unreadNotificationsCount = _notifications.count { !it.read }
    }

    // Accept an application
    fun acceptApplication(proposalId: String) {
        val proposal = _jobProposals.find { it.id == proposalId } ?: return
        updateProposalStatus(
            proposal = proposal,
            newStatus = ApplicationStatus.ACCEPTED,
            onSuccess = {
                Log.d("AppViewModel", "Application accepted successfully")
                loadJobProposals(proposal.jobId)
            },
            onFailure = {
                Log.e("AppViewModel", "Error accepting application")
            }
        )
    }

    // Reject an application
    fun rejectApplication(proposalId: String) {
        val proposal = _jobProposals.find { it.id == proposalId } ?: return
        updateProposalStatus(
            proposal = proposal,
            newStatus = ApplicationStatus.REJECTED,
            onSuccess = {
                Log.d("AppViewModel", "Application rejected successfully")
                loadJobProposals(proposal.jobId)
            },
            onFailure = {
                Log.e("AppViewModel", "Error rejecting application")
            }
        )
    }

    // Set application to interviewing status
    fun setApplicationToInterviewing(proposalId: String) {
        val proposal = _jobProposals.find { it.id == proposalId } ?: return
        updateProposalStatus(
            proposal = proposal,
            newStatus = ApplicationStatus.INTERVIEWING,
            onSuccess = {
                Log.d("AppViewModel", "Application set to interviewing successfully")
                loadJobProposals(proposal.jobId)
            },
            onFailure = {
                Log.e("AppViewModel", "Error setting application to interviewing")
            }
        )
    }

    // Update application status
    private fun updateApplicationStatus(proposalId: String, status: ApplicationStatus) {
        val db = FirebaseFirestore.getInstance()
        db.collection("proposals").document(proposalId)
            .update("status", status.name)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Application status updated to ${status.name}")
                // Find the job ID for this proposal
                val proposal = _jobProposals.find { it.id == proposalId }
                if (proposal != null) {
                    // Refresh the proposals list
                    loadJobProposals(proposal.jobId)
                }
                // Also refresh user proposals if needed
                loadUserProposals()
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error updating application status", e)
            }
    }

    private fun updateProposalStatus(
        proposal: Proposal,
        newStatus: ApplicationStatus,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("proposals").document(proposal.id)
            .update("status", newStatus.name)
            .addOnSuccessListener {
                // Create notification for the applicant
                val statusMessage = when (newStatus) {
                    ApplicationStatus.ACCEPTED -> "Your application for ${proposal.jobTitle} has been accepted!"
                    ApplicationStatus.REJECTED -> "Your application for ${proposal.jobTitle} has been declined."
                    ApplicationStatus.INTERVIEWING -> "You've been selected for an interview for ${proposal.jobTitle}. Expect a call soon!"
                    else -> "Your application status has been updated."
                }
                // Create notification for the applicant
                val notification = NotificationItem(
                    title = "Application Status Update",
                    message = statusMessage,
                    userId = proposal.applicantId,
                    type = "proposal_status",
                    relatedId = proposal.id
                )
                // Save notification to Firestore
                saveNotificationToFirestore(notification)
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    // Submit a job application
    fun submitJobApplication(
        job: Job,
        coverLetter: String,
        proposalText: String,
        cvUri: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val proposal = Proposal(
            id = UUID.randomUUID().toString(),
            jobId = job.id,
            jobTitle = job.title,
            applicantId = currentUser.uid,
            applicantName = currentUser.displayName ?: "",
            applicantEmail = currentUser.email ?: "",
            coverLetter = proposalText,
            cvUrl = cvUri,
            timestamp = Timestamp.now(),
            status = ApplicationStatus.PENDING,
        )
        // Add to Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("proposals").document(proposal.id)
            .set(proposal)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Job application submitted successfully")
                loadUserProposals()
                // Create notification for the applicant
                createNotification(
                    title = "Application Submitted",
                    message = "Your application for ${job.title} at ${job.company} has been submitted successfully.",
                    type = "application_submitted",
                    relatedId = proposal.id
                )
                // Create notification for the job poster
                val notificationForPoster = NotificationItem(
                    title = "New Application Received",
                    message = "You have received a new application from ${currentUser.displayName ?: "a candidate"} for ${job.title}.",
                    userId = job.uid,
                    type = "new_application",
                    relatedId = proposal.id
                )
                saveNotificationToFirestore(notificationForPoster)
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error submitting job application", e)
                onError(e)
            }
    }

    // Load notifications for current user
    fun loadNotifications() {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    Log.d("Notifications", "Loading notifications for user ${currentUser.uid}")
                    FirebaseFirestore.getInstance()
                        .collection("notifications")
                        .whereEqualTo("userId", currentUser.uid)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { documents ->
                            Log.d("Notifications", "Retrieved ${documents.size()} notifications")
                            _notifications.clear()
                            for (document in documents) {
                                try {
                                    val id = document.id
                                    val title = document.getString("title") ?: ""
                                    val message = document.getString("message") ?: ""
                                    val timestamp = document.getTimestamp("timestamp")
                                    val read = document.getBoolean("read") ?: false
                                    val type = document.getString("type") ?: ""
                                    val userId = document.getString("userId") ?: ""
                                    val relatedId = document.getString("relatedId")
                                    val notification = timestamp?.let {
                                        relatedId?.let { it1 ->
                                            NotificationItem(
                                                id = id,
                                                title = title,
                                                message = message,
                                                timestamp = it,
                                                read = read,
                                                type = type,
                                                userId = userId,
                                                relatedId = it1
                                            )
                                        }
                                    }
                                    if (notification != null) {
                                        _notifications.add(notification)
                                    }
                                    Log.d("Notifications", "Added notification: $title")
                                } catch (e: Exception) {
                                    Log.e(
                                        "Notifications",
                                        "Error parsing notification: ${e.message}"
                                    )
                                }
                            }
                            updateUnreadNotificationsCount()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Notifications", "Error loading notifications: ${e.message}")
                        }
                } else {
                    Log.d("Notifications", "No user logged in")
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Error in loadNotifications: ${e.message}")
            }
        }
    }

    // Clear all notifications
    fun clearAllNotifications() {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val batch = FirebaseFirestore.getInstance().batch()
                    // Get all notifications for the current user
                    FirebaseFirestore.getInstance()
                        .collection("notifications")
                        .whereEqualTo("userId", currentUser.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            // Add each document to the batch for deletion
                            for (document in documents) {
                                batch.delete(document.reference)
                            }
                            // Execute the batch delete
                            if (documents.size() > 0) {
                                batch.commit()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "Notifications",
                                            "Successfully cleared ${documents.size()} notifications"
                                        )
                                        // Clear the local list only after successful Firestore deletion
                                        _notifications.clear()
                                        updateUnreadNotificationsCount()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "Notifications",
                                            "Error clearing notifications: ${e.message}"
                                        )
                                    }
                            } else {
                                Log.d("Notifications", "No notifications to clear")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "Notifications",
                                "Error getting notifications to clear: ${e.message}"
                            )
                        }
                }
            } catch (e: Exception) {
                Log.e("Notifications", "Error in clearAllNotifications: ${e.message}")
            }
        }
    }

    // Delete a notification
    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("notifications")
                    .document(notificationId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("Notifications", "Successfully deleted notification $notificationId")
                        // Remove from local list only after successful Firestore deletion
                        _notifications.removeIf { it.id == notificationId }
                        updateUnreadNotificationsCount()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Notifications", "Error deleting notification: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e("Notifications", "Error in deleteNotification: ${e.message}")
            }
        }
    }

    // Mark a notification as read
    fun markNotificationAsRead(notificationId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("notifications").document(notificationId)
            .update("read", true)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Notification marked as read")
                // Update local notification
                val index = _notifications.indexOfFirst { it.id == notificationId }
                if (index != -1) {
                    val updatedNotification = _notifications[index].copy(read = true)
                    _notifications[index] = updatedNotification
                    updateUnreadNotificationsCount()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error marking notification as read", e)
            }
    }

    // Mark all notifications as read
    fun markAllNotificationsAsRead() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()
        // Get all unread notifications for the current user
        db.collection("notifications")
            .whereEqualTo("userId", currentUser.uid)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { documents ->
                // Create a batch update
                for (document in documents) {
                    val notificationRef = db.collection("notifications").document(document.id)
                    batch.update(notificationRef, "read", true)
                }
                // Commit the batch
                batch.commit()
                    .addOnSuccessListener {
                        Log.d("AppViewModel", "All notifications marked as read")
                        // Update local notifications
                        _notifications.forEachIndexed { index, notification ->
                            if (!notification.read) {
                                _notifications[index] = notification.copy(read = true)
                            }
                        }
                        updateUnreadNotificationsCount()
                    }
                    .addOnFailureListener { e ->
                        Log.e("AppViewModel", "Error marking all notifications as read", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Error getting unread notifications", e)
            }
    }

    // CHAT FUNCTIONALITY

    /**
     * Creates a new chat between users based on a proposal
     */


    private fun setupMessagesListener(chatId: String) {
        // Remove any existing listener

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        Log.d("AppViewModel", "Setting up messages listener for chat: $chatId")

        // Set up real-time listener for new messages
        messagesListener = db.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("AppViewModel", "Error listening for messages", e)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    Log.d(
                        "AppViewModel",
                        "Received message snapshot with ${snapshots.documents.size} messages"
                    )

                    val messagesList = mutableListOf<Message>()
                    for (document in snapshots.documents) {
                        try {
                            val message =
                                document.toObject(Message::class.java)?.copy(id = document.id)
                            if (message != null) {
                                Log.d(
                                    "AppViewModel",
                                    "Message from listener: ${message.id}, content: ${message.content}"
                                )
                                messagesList.add(message)

                                // Mark messages as read if they're not from current user
                                if (message.senderId != currentUser?.uid && !message.read) {
                                    db.collection("messages").document(message.id)
                                        .update("read", true)
                                        .addOnFailureListener { err ->
                                            Log.e(
                                                "AppViewModel",
                                                "Error marking message as read",
                                                err
                                            )
                                        }
                                }
                            }
                        } catch (ex: Exception) {
                            Log.e("AppViewModel", "Error parsing message data", ex)
                        }
                    }

                    // Update the UI with messages
                    if (messagesList.isNotEmpty()) {
                        Log.d("AppViewModel", "Updating UI with ${messagesList.size} messages")
                        _messages.clear()
                        _messages.addAll(messagesList)
                    }
                } else {
                    Log.d("AppViewModel", "No messages in snapshot")
                }
            }
    }

    fun getProposalsForJob(jobId: String): List<Proposal> {
        return jobProposals.filter { it.jobId == jobId }
    }

    // Load proposals for a specific job
    fun loadProposals(jobId: String) {
        isLoading.value = true

        firestore.collection("proposals")
            .whereEqualTo("jobId", jobId)
            .get()
            .addOnSuccessListener { documents ->
                val proposalsList = documents.mapNotNull { document ->
                    try {
                        val data = document.data
                        val statusString =
                            data["status"] as? String ?: ApplicationStatus.PENDING.name
                        val status = try {
                            ApplicationStatus.valueOf(statusString)
                        } catch (e: IllegalArgumentException) {
                            ApplicationStatus.PENDING
                        }

                        Proposal(
                            id = document.id,
                            jobId = data["jobId"] as? String ?: "",
                            jobTitle = data["jobTitle"] as? String ?: "",
                            applicantId = data["applicantId"] as? String ?: "",
                            applicantName = data["applicantName"] as? String ?: "",
                            applicantEmail = data["applicantEmail"] as? String ?: "",
                            applicantPhone = data["applicantPhone"] as? String ?: "",
                            coverLetter = data["coverLetter"] as? String ?: "",
                            cvUrl = data["cvUrl"] as? String ?: "",
                            portfolioUrl = data["portfolioUrl"] as? String ?: "",
                            status = status,
                            timestamp = data["timestamp"] as? Timestamp,
                            recruiterNotes = data["recruiterNotes"] as? String ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                _jobProposals.clear()
                _jobProposals.addAll(proposalsList)
                isLoading.value = false
            }
            .addOnFailureListener { e ->
                error.value = "Failed to load proposals: ${e.message}"
                isLoading.value = false
            }
    }


    // In sendMessage method


    // Method to create a chat from a proposal
    // Method to create a chat from a proposal
    // Method to create a chat from a proposal
    // Function to create a chat between a recruiter and an applicant for a specific job
    fun createChat(
        proposal: Proposal,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        // First check if a chat already exists for this proposal
        db.collection("chats")
            .whereEqualTo("proposalId", proposal.id)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Chat already exists, return its ID
                    onSuccess(documents.documents[0].id)
                } else {
                    // Get job details to include job title and recruiter info
                    db.collection("jobs").document(proposal.jobId)
                        .get()
                        .addOnSuccessListener { jobDocument ->
                            val job = jobDocument.toObject(Job::class.java)

                            // Get recruiter details
                            db.collection("users").document(job?.uid ?: "")
                                .get()
                                .addOnSuccessListener { recruiterDoc ->
                                    val recruiterEmail = recruiterDoc.getString("email") ?: ""
                                    val recruiterName = recruiterDoc.getString("name") ?: ""

                                    // Create a new chat
                                    val chatData = HashMap<String, Any>().apply {
                                        put("applicantId", proposal.applicantId)
                                        put("applicantEmail", proposal.applicantEmail)
                                        put("applicantName", proposal.applicantName)
                                        put("recruiterId", job?.uid ?: "")
                                        put("recruiterEmail", recruiterEmail)
                                        put("recruiterName", recruiterName)
                                        put("jobId", proposal.jobId)
                                        put("jobTitle", job?.title ?: proposal.jobTitle)
                                        put("proposalId", proposal.id)
                                        put("createdAt", Timestamp.now())
                                        put("lastMessage", "")
                                        put("lastMessageTimestamp", Timestamp.now())
                                        put("lastMessageSenderId", "")
                                        put("unreadApplicant", 0)
                                        put("unreadRecruiter", 0)
                                    }

                                    db.collection("chats")
                                        .add(chatData)
                                        .addOnSuccessListener { documentReference ->
                                            onSuccess(documentReference.id)
                                        }
                                        .addOnFailureListener { e ->
                                            onError(e)
                                        }
                                }
                                .addOnFailureListener { e ->
                                    onError(e)
                                }
                        }
                        .addOnFailureListener { e ->
                            onError(e)
                        }
                }
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    // Function to load chats for the current user


    // Function to load chats for the current user
    fun loadChats(userId: String) {
        isLoading.value = true
        Log.d("ChatDebug", "Loading chats for user: $userId")

        val db = FirebaseFirestore.getInstance()

// Clear existing chats
        _chats.clear()

// Create a list to hold all chats
        val allChats = mutableListOf<Chat>()

// Counter to track when both queries are complete
        val queryCounter = AtomicInteger(0)

// Function to process results after both queries complete
        val processResults = {
            // Sort chats by last message timestamp (newest first)
            allChats.sortByDescending { it.lastMessageTimestamp }

            // Update the UI with chats
            _chats.clear()
            _chats.addAll(allChats)
            Log.d("ChatDebug", "Updated chats list with ${allChats.size} chats")

            isLoading.value = false
        }

// Query chats where user is applicant
        db.collection("chats")
            .whereEqualTo("applicantId", userId)
            .get()
            .addOnSuccessListener { applicantChats ->
                Log.d("ChatDebug", "Found ${applicantChats.size()} chats where user is applicant")

                // Add chats where user is applicant
                for (document in applicantChats) {
                    try {
                        val chat = document.toObject(Chat::class.java).copy(id = document.id)
                        Log.d(
                            "ChatDebug",
                            "Found applicant chat: ${chat.id}, job: ${chat.jobTitle}"
                        )
                        allChats.add(chat)
                    } catch (e: Exception) {
                        Log.e(
                            "ChatDebug",
                            "Error parsing applicant chat data for doc ${document.id}",
                            e
                        )
                    }
                }

                // Increment counter and check if both queries are complete
                if (queryCounter.incrementAndGet() == 2) {
                    processResults()
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatDebug", "Error loading applicant chats", e)
                error.value = "Failed to load chats: ${e.message}"

                // Increment counter and check if both queries are complete
                if (queryCounter.incrementAndGet() == 2) {
                    processResults()
                }
            }

// Query chats where user is recruiter
        db.collection("chats")
            .whereEqualTo("recruiterId", userId)
            .get()
            .addOnSuccessListener { recruiterChats ->
                Log.d("ChatDebug", "Found ${recruiterChats.size()} chats where user is recruiter")

                // Add chats where user is recruiter
                for (document in recruiterChats) {
                    try {
                        val chat = document.toObject(Chat::class.java).copy(id = document.id)
                        Log.d(
                            "ChatDebug",
                            "Found recruiter chat: ${chat.id}, job: ${chat.jobTitle}"
                        )
                        allChats.add(chat)
                    } catch (e: Exception) {
                        Log.e(
                            "ChatDebug",
                            "Error parsing recruiter chat data for doc ${document.id}",
                            e
                        )
                    }
                }

                // Increment counter and check if both queries are complete
                if (queryCounter.incrementAndGet() == 2) {
                    processResults()
                }
            }
            .addOnFailureListener { e ->
                Log.e("ChatDebug", "Error loading recruiter chats", e)
                error.value = "Failed to load chats: ${e.message}"

                // Increment counter and check if both queries are complete
                if (queryCounter.incrementAndGet() == 2) {
                    processResults()
                }
            }
            }

        fun sendMessage(chatId: String, content: String, senderId: String) {
            if (content.isBlank()) return

            Log.d("ChatDebug", "Sending message to chat $chatId: $content")

            val db = FirebaseFirestore.getInstance()
            val timestamp = Timestamp.now()

            // Create the message
            val message = hashMapOf(
                "chatId" to chatId,
                "senderId" to senderId,
                "content" to content,
                "timestamp" to timestamp,
                "read" to false
            )

            // Add the message to Firestore
            db.collection("messages")
                .add(message)
                .addOnSuccessListener { documentRef ->
                    Log.d("ChatDebug", "Message sent successfully with ID: ${documentRef.id}")

                    // Get the chat to determine if sender is applicant or recruiter
                    db.collection("chats").document(chatId)
                        .get()
                        .addOnSuccessListener { document ->
                            val chat = document.toObject(Chat::class.java)
                            if (chat != null) {
                                val isApplicant = chat.applicantId == senderId
                                val updateField =
                                    if (isApplicant) "unreadRecruiter" else "unreadApplicant"

                                // Update the chat with last message info and increment unread counter
                                val updates = hashMapOf<String, Any>(
                                    "lastMessage" to content,
                                    "lastMessageTimestamp" to timestamp,
                                    "lastMessageSenderId" to senderId,
                                    updateField to FieldValue.increment(1)
                                )

                                db.collection("chats").document(chatId)
                                    .update(updates)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "ChatDebug",
                                            "Chat document updated with last message"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("ChatDebug", "Failed to update chat document", e)
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatDebug", "Failed to get chat document", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("ChatDebug", "Failed to send message", e)
                    error.value = "Failed to send message: ${e.message}"
                }
        }

        fun clearMessagesListener() {
            messagesListener?.remove()
            messagesListener = null
            Log.d("ChatDebug", "Messages listener cleared")
        }

        fun setCurrentChat(chatId: String) {
            val db = FirebaseFirestore.getInstance()
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return

// Clear existing messages
            _messages.clear()

// Remove any existing listener


            Log.d("ChatDebug", "Setting up messages listener for chat: $chatId")

// Set up a new listener for messages
            messagesListener = db.collection("messages")
                .whereEqualTo("chatId", chatId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.e("ChatDebug", "Error listening for messages: ${e.message}", e)
                        error.value = "Failed to load messages: ${e.message}"
                        return@addSnapshotListener
                    }

                    Log.d(
                        "ChatDebug",
                        "Message snapshot received: ${snapshots?.size() ?: 0} documents"
                    )

                    if (snapshots != null && !snapshots.isEmpty) {
                        val messagesList = mutableListOf<Message>()
                        val batch = db.batch()
                        var hasUnreadMessages = false

                        for (doc in snapshots) {
                            try {
                                val message = doc.toObject(Message::class.java).copy(id = doc.id)
                                Log.d(
                                    "ChatDebug",
                                    "Message: ${message.id}, content: ${message.content}"
                                )
                                messagesList.add(message)

                                // Mark messages as read if they're not from current user
                                if (message.senderId != currentUser.uid && !message.read) {
                                    val messageRef = db.collection("messages").document(message.id)
                                    batch.update(messageRef, "read", true)
                                    hasUnreadMessages = true
                                }
                            } catch (e: Exception) {
                                Log.e("ChatDebug", "Error parsing message data", e)
                            }
                        }

                        // Commit the batch update if there are unread messages
                        if (hasUnreadMessages) {
                            batch.commit()
                        }

                        // Update the UI with messages
                        _messages.clear()
                        _messages.addAll(messagesList)
                        Log.d(
                            "ChatDebug",
                            "Updated messages list with ${messagesList.size} messages"
                        )
                    } else {
                        Log.d("ChatDebug", "No messages in snapshot")
                    }

                    isLoading.value = false
                }

// Also update the unread counter for the current user
            db.collection("chats").document(chatId)
                .get()
                .addOnSuccessListener { chatDocument ->
                    val chat = chatDocument.toObject(Chat::class.java)
                    if (chat != null) {
                        val isApplicant = chat.applicantId == currentUser.uid
                        val updateField = if (isApplicant) "unreadApplicant" else "unreadRecruiter"

                        // Update the unread counter
                        db.collection("chats").document(chatId)
                            .update(updateField, 0)
                    }
                }




            // Load user profile
            fun loadUserProfile(userId: String) {
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            try {
                                val userProfile = document.toObject(User::class.java)
                                currentUserProfile.value = userProfile
                            } catch (e: Exception) {
                                Log.e("AppViewModel", "Error parsing user profile", e)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("AppViewModel", "Error loading user profile", e)
                    }
            }


        }}