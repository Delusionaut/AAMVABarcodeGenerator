package com.aamva.barcodegenerator.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ModernPrimary.copy(alpha = 0.8f),
                        ModernAccent.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    hasGradient: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (hasGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(ModernPrimary, ModernAccent)
                        )
                    )
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    startColor: Color = ModernPrimary,
    endColor: Color = ModernAccent,
    thickness: Dp = 2.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .clip(RectangleShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(startColor, endColor)
                )
            )
    )
}

@Composable
fun GlassBorder(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.2f),
    width: Dp = 1.dp,
    cornerRadius: Dp = 8.dp,
    dashed: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.Transparent)
            .border(
                width = width,
                color = color,
                shape = RoundedCornerShape(cornerRadius),
                pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(10f, 10f)) else null
            )
    )
}
