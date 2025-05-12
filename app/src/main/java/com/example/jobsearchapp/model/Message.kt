package com.example.jobsearchapp.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val chatId: String = "",
    val content: String = "",
    val senderId: String = "",
    val recruiterName: String= "",
    val timestamp: Timestamp? = null,
    val read: Boolean = false,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileType: String? = null,
    val type: MessageType = MessageType.TEXT
)
