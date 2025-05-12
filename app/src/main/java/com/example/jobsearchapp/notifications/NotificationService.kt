package com.example.jobsearchapp.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.jobsearchapp.MainActivity
import com.example.jobsearchapp.R
import com.example.jobsearchapp.Proposal
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class NotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 101
    private val channelId = "job_applications"

    init {
        createNotificationChannel()
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Job Applications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for job application updates"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showJobNotification(proposal: Proposal) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NOTIFICATION_TYPE", "PROPOSAL")
            putExtra("JOB_ID", proposal.jobId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            proposal.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = when (proposal.status) {
            ApplicationStatus.SENT -> "Application Sent Successfully!"
            ApplicationStatus.VIEWED -> "Application Viewed"
            ApplicationStatus.ACCEPTED -> "Congratulations!"
            ApplicationStatus.REJECTED -> "Application Status Update"
            ApplicationStatus.NEW_PROPOSAL -> "New Application"
            ApplicationStatus.INTERVIEWING -> "Interview Scheduled"
            ApplicationStatus.PENDING -> "Application Pending"
        }

        val message = when (proposal.status) {
            ApplicationStatus.SENT -> "Your application for ${proposal.jobTitle} has been submitted successfully."
            ApplicationStatus.VIEWED -> "Great news! Your application for ${proposal.jobTitle} has been viewed."
            ApplicationStatus.ACCEPTED -> "🎉 Your application for ${proposal.jobTitle} has been accepted! Next steps coming soon."
            ApplicationStatus.REJECTED -> "Your application for ${proposal.jobTitle} has been reviewed. Unfortunately, it was not selected."
            ApplicationStatus.NEW_PROPOSAL -> "New application received for ${proposal.jobTitle} from ${proposal.applicantEmail}"
            ApplicationStatus.INTERVIEWING -> "You've been selected for an interview for ${proposal.jobTitle}. Check your email for details."
            ApplicationStatus.PENDING -> "Your application for ${proposal.jobTitle} is pending review."
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notififcation) // Make sure to create this icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(proposal.id.hashCode(), notification)
    }

    fun sendEmailNotification(proposal: Proposal, jobTitle: String) {
        val functions = Firebase.functions

        val title = when (proposal.status) {
            ApplicationStatus.ACCEPTED -> "Application Accepted!"
            ApplicationStatus.REJECTED -> "Application Status Update"
            ApplicationStatus.INTERVIEWING -> "Interview Request"
            else -> "Application Update"
        }

        val message = when (proposal.status) {
            ApplicationStatus.ACCEPTED -> "Congratulations! Your application for $jobTitle has been accepted."
            ApplicationStatus.REJECTED -> "We're sorry, your application for $jobTitle has been declined."
            ApplicationStatus.INTERVIEWING -> "You've been selected for an interview for $jobTitle. Expect a call soon!"
            else -> "Your application status for $jobTitle has been updated."
        }

        val data = hashMapOf(
            "recipientEmail" to proposal.applicantEmail,
            "subject" to title,
            "message" to message,
            "jobTitle" to jobTitle,
            "status" to proposal.status.toString()
        )

        functions
            .getHttpsCallable("sendApplicationStatusEmail")
            .call(data)
            .addOnSuccessListener {
                // Email sent successfully
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}

enum class ApplicationStatus(val displayName: String) {
    SENT("Sent"),
    VIEWED("Viewed"),
    INTERVIEWING("Interviewing"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    NEW_PROPOSAL("New"),
    PENDING("Pending"),
}
