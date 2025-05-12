package com.example.jobsearchapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationBadge(
    notificationCount: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (notificationCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }
    }
}

// Alternative implementation if BadgedBox is causing issues
@Composable
fun NotificationBadgeAlternative(
    notificationCount: Int,
    onClick: () -> Unit
) {
    Box {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }

        if (notificationCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = 10.dp, y = (-6).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}
