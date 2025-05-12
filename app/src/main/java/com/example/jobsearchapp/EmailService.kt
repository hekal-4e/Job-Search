package com.example.jobsearchapp.notifications

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.jobsearchapp.Proposal
import com.example.jobsearchapp.model.Job

class EmailService(private val context: Context) {

    fun sendApplicationConfirmationEmail(proposal: Proposal, job: Job) {
        val subject = "Application Confirmation: ${job.title} at ${job.company}"
        val body = """
            Dear Applicant,
            
            Thank you for applying to the ${job.title} position at ${job.company}.
            
            Your application has been received and is currently under review. We will contact you once we have an update on your application status.
            
            Application Details:
            - Job Title: ${job.title}
            - Company: ${job.company}
            - Location: ${job.location}
            - Application Date: ${java.text.SimpleDateFormat("MMM dd, yyyy").format(proposal.timestamp)}
            
            Best regards,
            The Job Search App Team
        """.trimIndent()

        sendEmail(proposal.applicantEmail, subject, body)
    }

    fun sendApplicationStatusUpdateEmail(proposal: Proposal, job: Job) {
        val subject = when (proposal.status) {
            ApplicationStatus.ACCEPTED -> "Congratulations! Your application for ${job.title} has been accepted"
            ApplicationStatus.REJECTED -> "Update on your application for ${job.title}"
            else -> "Application Status Update: ${job.title}"
        }

        val body = when (proposal.status) {
            ApplicationStatus.ACCEPTED -> """
                Dear Applicant,
                
                Congratulations! We are pleased to inform you that your application for the ${job.title} position at ${job.company} has been accepted.
                
                The hiring team will contact you shortly to discuss the next steps in the hiring process.
                
                Best regards,
                The Job Search App Team
            """.trimIndent()

            ApplicationStatus.REJECTED -> """
                Dear Applicant,
                
                Thank you for your interest in the ${job.title} position at ${job.company}.
                
                After careful consideration, we regret to inform you that your application has not been selected for further consideration at this time.
                
                We appreciate your interest in ${job.company} and wish you success in your job search.
                
                Best regards,
                The Job Search App Team
            """.trimIndent()

            else -> """
                Dear Applicant,
                
                There has been an update to your application for the ${job.title} position at ${job.company}.
                
                Please log in to your Job Search App account to view the details.
                
                Best regards,
                The Job Search App Team
            """.trimIndent()
        }

        sendEmail(proposal.applicantEmail, subject, body)
    }

    fun sendNewApplicationNotificationEmail(proposal: Proposal, job: Job) {
        val subject = "New Application Received: ${job.title}"
        val body = """
            Dear Recruiter,
            
            You have received a new application for the ${job.title} position.
            
            Application Details:
            - Applicant: ${proposal.applicantEmail}
            - Application Date: ${java.text.SimpleDateFormat("MMM dd, yyyy").format(proposal.timestamp)}
            
            Please log in to your Job Search App account to review the application.
            
            Best regards,
            The Job Search App Team
        """.trimIndent()

        sendEmail(job.posterEmail, subject, body)
    }

    private fun sendEmail(recipient: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
