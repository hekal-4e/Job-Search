package com.example.jobsearchapp.ui.profile

import OrangeTheme.Orange
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.TabRowDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    onUpdatePassword: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var showUpdatePassword by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showUpdatePasswordDialog by remember { mutableStateOf(false) }

    if (showUpdatePassword) {
        // Use the existing UpdatePasswordScreen from UpdatePasswordScreen.kt
        UpdatePasswordScreen(onBack = { showUpdatePassword = false })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            SettingsItem(
                title = "Notifications",
                icon = Icons.Filled.Notifications,
                showSwitch = true,
                switchChecked = notificationsEnabled,
                onSwitchToggle = { notificationsEnabled = it }
            )
            TabRowDefaults.Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            SettingsItem(
                title = "Dark mode",
                icon = Icons.Filled.DarkMode,
                showSwitch = true,
                switchChecked = darkModeEnabled,
                onSwitchToggle = { darkModeEnabled = it }
            )
            TabRowDefaults.Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            SettingsItem(
                title = "Password",
                icon = Icons.Filled.Password,
                showSwitch = false,
                onClick = { showUpdatePasswordDialog = true }
            )
            TabRowDefaults.Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            SettingsItem(
                title = "Logout",
                icon = Icons.Filled.Logout,
                showSwitch = false,
                onClick = {
                    showLogoutDialog = true
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {  },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E275B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "SAVE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (showLogoutDialog) {
            // Use the existing LogoutBottomSheet from DialogScreen.kt
            LogoutBottomSheet(
                onDismissRequest = {
                    showLogoutDialog = false
                },
                onLogoutConfirmed = {
                    onLogout()
                    showLogoutDialog = false
                }
            )
        }
        if(showUpdatePasswordDialog) {
            UpdatePasswordBottomSheet(
                onDismissRequest = { showUpdatePasswordDialog = false },
                onConfirmed = {

                    showUpdatePasswordDialog = false
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector? = null,
    iconResId: Int? = null,
    showSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchToggle: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(45.dp)
            .padding(vertical = 8.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        } else if (iconResId != null) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (showSwitch) {
            Switch(
                checked = switchChecked,
                onCheckedChange = onSwitchToggle,
                thumbContent = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (switchChecked) Orange else Color.LightGray,
                                shape = CircleShape
                            )
                    )
                }
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
