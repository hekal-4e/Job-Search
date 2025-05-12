package com.example.jobsearchapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp

@Composable
fun BackgroundContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A014F), // Dark Blue Top
                        Color(0xFF5B5F97)  // Light Blue Bottom
                    )
                )
            )
    ) {
        // Add glowing blurred circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            withTransform({
                translate(left = -300f, top = -300f)
            }) {
                drawCircle(
                    color = Color(0xFF845EF7),
                    radius = 400f
                )
            }

            withTransform({
                translate(left = size.width - 100f, top = size.height - 400f)
            }) {
                drawCircle(
                    color = Color(0xFF5B5F97),
                    radius = 300f
                )
            }

            withTransform({
                translate(left = size.width / 2f, top = size.height / 3f)
            }) {
                drawCircle(
                    color = Color(0xFFACC7FF),
                    radius = 200f
                )
            }
        }

        // Add blur layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(120.dp)
        )

        // Actual screen content on top
        content()
    }
}