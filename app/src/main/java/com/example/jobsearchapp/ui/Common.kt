package com.example.jobsearchapp.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isTextButton: Boolean = false,
    content: @Composable (RowScope.() -> Unit)
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Button scale animation"
    )

    val buttonModifier = modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

    if (isTextButton) {
        TextButton(
            onClick = {
                isPressed = true
                onClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E275B),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = buttonModifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            content = content,

        )
    } else {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E275B),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = buttonModifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            content = content
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}
