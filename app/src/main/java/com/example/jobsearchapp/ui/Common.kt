package com.example.jobsearchapp.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
            modifier = buttonModifier,
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            content = content
        )
    } else {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = buttonModifier,
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
