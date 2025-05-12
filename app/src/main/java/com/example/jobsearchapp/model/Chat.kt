package com.example.jobsearchapp.model

import com.google.firebase.Timestamp




data class Chat(
    val id: String = "",
    val applicantId: String = "",
    val applicantEmail: String = "",
    val applicantName: String = "",
    val recruiterId: String = "",
    val recruiterEmail: String = "",
    val recruiterName: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val proposalId: String = "",
    val createdAt: Timestamp? = null,
    val lastMessage: String = "",
    val lastMessageTimestamp: Timestamp? = null,
    val lastMessageSenderId: String = "",
    val unreadApplicant: Int = 0,
    val unreadRecruiter: Int = 0
)



