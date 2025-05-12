package com.example.jobsearchapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsListScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val chats = viewModel.chats
    val isLoading = viewModel.isLoading.value
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { userId ->
            viewModel.loadChats(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (chats.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No messages yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When you apply for jobs or receive applications, your conversations will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Chats list
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chats) { chat ->
                        ChatItem(
                            chat = chat,
                            currentUserId = currentUser?.uid ?: "",
                            onClick = {
                                navController.navigate("chat/${chat.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    chat: com.example.jobsearchapp.model.Chat,
    currentUserId: String,
    onClick: () -> Unit
) {
    val isRecruiter = currentUserId == chat.recruiterId

    val otherPersonName = if (isRecruiter) {
        chat.applicantName.ifEmpty { chat.applicantEmail }
    } else {
        chat.recruiterName.ifEmpty { chat.recruiterEmail }
    }

    // Format timestamp
    val formattedTime = remember(chat.lastMessageTimestamp) {
        chat.lastMessageTimestamp?.toDate()?.let { date ->
            val now = Calendar.getInstance()
            val messageTime = Calendar.getInstance().apply { time = date }
            when {
                // Today
                now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) &&
                        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) -> {
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
                }
                // This week
                now.get(Calendar.WEEK_OF_YEAR) == messageTime.get(Calendar.WEEK_OF_YEAR) &&
                        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) -> {
                    SimpleDateFormat("EEE", Locale.getDefault()).format(date)
                }
                // This year
                now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) -> {
                    SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
                }
                // Older
                else -> {
                    SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(date)
                }
            }
        } ?: ""
    }

    val unreadCount = if (isRecruiter) chat.unreadRecruiter else chat.unreadApplicant

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder (can be replaced with actual avatar)
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = otherPersonName.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = otherPersonName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (unreadCount > 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (chat.lastMessage.isNotEmpty()) chat.lastMessage else chat.jobTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (unreadCount > 0)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Unread indicator
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(start = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Divider(
            modifier = Modifier.padding(start = 80.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}
