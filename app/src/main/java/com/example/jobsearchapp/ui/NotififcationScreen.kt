package com.example.jobsearchapp.ui

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.Timestamp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.example.jobsearchapp.model.NotificationItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val notifications = viewModel.notifications
    val context = LocalContext.current
    var isClearing by remember { mutableStateOf(false) }

    // Load notifications when the screen is first displayed
    LaunchedEffect(key1 = true) {
        viewModel.loadNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (notifications.isNotEmpty() && !isClearing) {
                        IconButton(onClick = {
                            isClearing = true
                            viewModel.clearAllNotifications()
                            Toast.makeText(context, "Clearing notifications...", Toast.LENGTH_SHORT).show()
                            // Reset the clearing state after a delay
                            Handler(Looper.getMainLooper()).postDelayed({
                                isClearing = false
                            }, 2000)
                        }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear All"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No notifications yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = notifications,
                        key = { it.id } // Use stable keys to prevent recomposition issues
                    ) { notification ->
                        var isDeleting by remember { mutableStateOf(false) }
                        NotificationItemCard(
                            notification = notification,
                            onMarkAsRead = {
                                viewModel.markNotificationAsRead(notification.id)
                            },
                            onDelete = {
                                if (!isDeleting) {
                                    isDeleting = true
                                    viewModel.deleteNotification(notification.id)
                                    // Reset after a delay
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        isDeleting = false
                                    }, 1000)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItemCard(
    notification: NotificationItem,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    // Safely handle the timestamp with null checks
    val formattedDate = when {
        notification.timestamp == null -> "Unknown date"
        notification.timestamp is Long -> dateFormat.format(Date(notification.timestamp as Long))
        notification.timestamp is com.google.firebase.Timestamp -> dateFormat.format((notification.timestamp as com.google.firebase.Timestamp).toDate())
        notification.timestamp is Date -> dateFormat.format(notification.timestamp as Date)
        else -> "Unknown date"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.read)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (!notification.read) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!notification.read) {
                    TextButton(onClick = onMarkAsRead) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = "Mark as read"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark as read")
                    }
                }
                TextButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}
