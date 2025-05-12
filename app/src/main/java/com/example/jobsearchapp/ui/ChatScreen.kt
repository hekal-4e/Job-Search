package com.example.jobsearchapp.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jobsearchapp.AppViewModel
import com.example.jobsearchapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: AppViewModel,
    chatId: String
) {
    val messages = viewModel.messages
    val isLoading = viewModel.isLoading.value
    val currentUser = FirebaseAuth.getInstance().currentUser
    val chats = viewModel.chats
    val chat = chats.find { it.id == chatId }

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()



    LaunchedEffect(chatId) {
        Log.d("ChatScreen", "Setting up chat with ID: $chatId")
        viewModel.setCurrentChat(chatId)
    }

    LaunchedEffect(messages.size) {
        Log.d("ChatScreen", "Messages updated: ${messages.size} messages")
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val otherPersonName = if (currentUser?.uid == chat?.recruiterId) {
                            chat?.applicantName?.ifEmpty { chat.applicantEmail }
                        } else {
                            chat?.recruiterName?.ifEmpty { chat?.recruiterEmail }
                        } ?: "Chat"

                        Text(text = otherPersonName)

                        chat?.jobTitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearMessagesListener()
                        navController.popBackStack()
                    }) {
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
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (messages.isEmpty()) {
                        // Show empty state
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "No messages yet",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Start the conversation by sending a message",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            state = listState,
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(messages) { message ->
                                val isCurrentUser = message.senderId == currentUser?.uid
                                MessageItem(
                                    message = message,
                                    isCurrentUser = isCurrentUser
                                )
                            }
                        }
                    }

                    // Message input
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Type a message") },
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (messageText.isNotBlank() && currentUser != null) {
                                        viewModel.sendMessage(chatId, messageText, currentUser.uid)
                                        messageText = ""
                                    }
                                },
                                shape = CircleShape,
                                modifier = Modifier.size(48.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = "Send"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearMessagesListener()
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    isCurrentUser: Boolean
) {
    val backgroundColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val alignment = if (isCurrentUser) {
        Alignment.End
    } else {
        Alignment.Start
    }

    val shape = if (isCurrentUser) {
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    }

    val formattedTime = remember(message.timestamp) {
        message.timestamp?.toDate()?.let { date ->
            val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
            sdf.format(date)
        } ?: ""
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = backgroundColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor
                )
            }
        }

        Text(
            text = formattedTime,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(4.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
