package com.aamva.barcodegenerator.ui.theme

import androidx.compose.animation.core.*

object SmoothAnimations {
    val FastOutSlowIn: FloatAnimationSpec<Float> = tween(300, easing = FastOutSlowInEasing)
    val FastOutLinearIn: FloatAnimationSpec<Float> = tween(250, easing = FastOutLinearInEasing)
    val LinearOutSlowIn: FloatAnimationSpec<Float> = tween(350, easing = LinearOutSlowInEasing)
    val EaseInOut: FloatAnimationSpec<Float> = tween(400, easing = AnticipateOvershootEasing)
    
    val Spring: FloatAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val FadeIn: FloatAnimationSpec<Float> = fadeIn(animationSpec = FastOutSlowIn)
    val FadeOut: FloatAnimationSpec<Float> = fadeOut(animationSpec = FastOutLinearIn)
    
    object Presets {
        val ButtonPress = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
        val CardFlip = tween(600, easing = FastOutSlowInEasing)
    }
}
