package com.aamva.barcodegenerator.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter

// 🎨 **MODERN UI COMPONENTS**
// Industry-leading components with premium interactions

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                onClick = { onClick?.invoke() },
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }
            
            subtitle?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(48.dp)
            .then(AdvancedGlass.PremiumGlassCard(
                blurRadius = 15f,
                gradientStart = MaterialTheme.colorScheme.primary,
                gradientEnd = MaterialTheme.colorScheme.secondary,
                cornerRadius = 16f
            )),
        colors = androidx.compose.material3.ButtonColors(
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    isEnabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .heightIn(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(4.dp),
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = isError,
        enabled = isEnabled,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}

@Composable
fun StatusIndicator(
    status: Status,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        Status.Success -> MaterialTheme.colorScheme.success
        Status.Error -> MaterialTheme.colorScheme.error
        Status.Warning -> MaterialTheme.colorScheme.warning
        Status.Info -> MaterialTheme.colorScheme.info
        Status.Loading -> MaterialTheme.colorScheme.primary
    }
    
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(RoundedCornerShape(50%).fillMaxWidth())
            .background(color)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(50%))
    )
}

enum class Status {
    Success, Error, Warning, Info, Loading
}

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 3.dp
    )
}

@Composable
fun SuccessAnimation(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    
    LaunchedEffect(true) {
        animate(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = SmoothAnimations.Presets.ButtonPress
        ) { value ->
            scale = value
        }
        rotation = 360f
    }
    
    Box(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation
            )
    ) {
        content()
    }
}

@Composable
fun ErrorDialog(
    onDismiss: () -> Unit,
    message: String,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error", style = MaterialTheme.typography.headlineMedium) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            GlassButton(
                onClick = onDismiss,
                text = "OK",
                modifier = Modifier.widthIn(100.dp)
            )
        },
        shape = RoundedCornerShape(20.dp),
        colors = AlertDialogDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun ModernToast(
    message: String,
    modifier: Modifier = Modifier,
    duration: Int = 3000
) {
    var visible by remember { mutableStateOf(true) }
    
    if (visible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp)
                .drawWithContent {
                    // Draw toast background
                    drawRect(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        alpha = 0.9f
                    )
                    drawContent()
                }
                .clip(RoundedCornerShape(16.dp))
                .drawBehind {
                    // Add subtle shadow
                    drawRect(
                        Color.Black.copy(alpha = 0.2f),
                        topLeft = Offset(0f, size.height * 0.1f),
                        size = size
                    )
                }
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
        
        LaunchedEffect(true) {
            delay(duration.toLong())
            visible = false
        }
    }
}