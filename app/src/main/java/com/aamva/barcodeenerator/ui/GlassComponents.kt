import com.aamva.barcodegenerator.ui.theme.ModernComponents
package com.aamva.barcodegenerator.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 🎨 **GLASSMORPHISM COMPONENTS**
// Industry-leading frosted glass effects

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    blurRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.White.copy(alpha = 0.2f),
    gradientStart: Color = ModernPrimaryGradientStart,
    gradientEnd: Color = ModernPrimaryGradientEnd,
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientStart, gradientEnd)
                )
            )
            .blur(blurRadius)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                } else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 8.dp,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    hasGradient: Boolean = false,
    gradientStart: Color = ModernPrimaryGradientStart,
    gradientEnd: Color = ModernPrimaryGradientEnd,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .then(
                if (hasGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(gradientStart, gradientEnd)
                        )
                    )
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .shadow(elevation, RoundedCornerShape(cornerRadius))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun GlassChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val chipModifier = if (onClick != null) {
        modifier
            .clickable(onClick = onClick)
            .hoverable()
    } else modifier
    
    GlassCard(
        modifier = chipModifier.padding(horizontal = 4.dp),
        cornerRadius = 12.dp,
        borderWidth = 0.5.dp,
        borderColor = Color.White.copy(alpha = 0.3f),
        gradientStart = Color.White.copy(alpha = 0.1f),
        gradientEnd = Color.Transparent
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.invoke()
            androidx.compose.material3.Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    startColor: Color = ModernPrimary,
    endColor: ModernAccent,
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
