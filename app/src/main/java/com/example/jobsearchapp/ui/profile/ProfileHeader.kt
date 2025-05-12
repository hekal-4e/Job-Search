package com.example.jobsearchapp.ui.profile


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.jobsearchapp.R
import com.example.jobsearchapp.UserProfile

@Composable
fun ProfileHeader(
    onChangeProfilePicture: () -> Unit = {},
    userProfile: UserProfile,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_1),
                contentDescription = "Cover Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Button(
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA5A1BD).copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.pencil_vec),
                        contentDescription = "Edit profile",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit profile",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(135.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onChangeProfilePicture() },
            contentAlignment = Alignment.Center
        ) {
            if (userProfile.profileImageUrl != null) {
                // Display existing profile image from URL
                AsyncImage(
                    model = userProfile.profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Display placeholder
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Image",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Small camera icon or edit indicator
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change Profile Picture",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        Text(
            text = "${userProfile.firstName ?: ""} ${userProfile.lastName ?: ""}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Job Title
//        Text(
//            text = "No job title".also { userProfile.jobTitle = it },
//            style = MaterialTheme.typography.titleMedium,
//            color = MaterialTheme.colorScheme.onSurfaceVariant
//        )

        Spacer(modifier = Modifier.height(4.dp))

        // Location
        Text(
            text = userProfile.location ?: "No location",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Button
//        Button(
//            onClick = onEditClick,
//            shape = RoundedCornerShape(8.dp),
//            modifier = Modifier
//                .fillMaxWidth(0.9f)
//                .height(50.dp)
//        ) {
//            Text("Edit Basic Info")
//        }
    }
}




//@Composable
//fun ProfileHeader(
//    userProfile: UserProfile,
//    onEditClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(250.dp)
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.img_1),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
//        )
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .align(Alignment.TopStart)
//        ) {
//
//            Image(
//                painter = painterResource(id = R.drawable.ic_launcher_background),
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column {
//                Text(
//                    text = userProfile.name,
//                    style = MaterialTheme.typography.titleLarge,
//                    color = Color.White,
//                    fontSize = 18.sp
//                )
//                Text(
//                    text = userProfile.location,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.Gray
//                )
//            }
//
//        }
//
//    }
//}