package com.example.jobsearchapp.model

import com.google.firebase.Timestamp
import java.util.Date



data class NotificationItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val read: Boolean = false,
    val type: String = "",
    val relatedId: String = "",
    val timestamp: Timestamp = Timestamp.now()


)



