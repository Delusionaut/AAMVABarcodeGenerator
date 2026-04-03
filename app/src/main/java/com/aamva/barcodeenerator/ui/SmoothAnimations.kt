package com.aamva.barcodegenerator.ui.theme

import androidx.compose.animation.core.*
// 🎨 **SMOOTH ANIMATIONS & MICRO-INTERACTIONS**
// Professional-grade animations for a polished user experience

object SmoothAnimations {
    
    // Fast out slow in - Material Design standard
    val FastOutSlowIn: FloatAnimationSpec<Float> = 
        tween(300, easing = FastOutSlowInEasing)
    
    // Fast out linear in - For quick transitions
    val FastOutLinearIn: FloatAnimationSpec<Float> = 
        tween(250, easing = FastOutLinearInEasing)
    
    // Linear out slow in - For entering elements
    val LinearOutSlowIn: FloatAnimationSpec<Float> = 
        tween(350, easing = LinearOutSlowInEasing)
    
    // Ease in out - For balanced animations
    val EaseInOut: FloatAnimationSpec<Float> = 
        tween(400, easing = AnticipateOvershootEasing)
    
    // Spring animation - For bouncy, responsive interactions
    val Spring: FloatAnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    // Shared transition - For coordinated element movements
    val SharedTransition: TransitionSpec<AnimationVector1D> = 
        tween(500, easing = FastOutSlowInEasing)
    
    // Fade in animation - For content appearance
    val FadeIn: FloatAnimationSpec<Float> = 
        fadeIn(animationSpec = FastOutSlowIn)
    
    // Fade out animation - For content disappearance
    val FadeOut: FloatAnimationSpec<Float> = 
        fadeOut(animationSpec = FastOutLinearIn)
    
    // Scale animation - For button presses and popups
    val ScaleUp: FloatAnimationSpec<Float> = 
        spring(dampingRatio = Spring.DampingRatioNoBouncy)
    
    val ScaleDown: FloatAnimationSpec<Float> = 
        spring(dampingRatio = Spring.DampingRatioNoBouncy)
    
    // Slide animation - For drawer and panel transitions
    val SlideIn: FloatAnimationSpec<Float> = 
        slideIn(
            { fullSize -> fullSize / 2 },
            animationSpec = FastOutSlowIn
        )
    
    val SlideOut: FloatAnimationSpec<Float> = 
        slideOut(
            { fullSize -> -fullSize / 4 },
            animationSpec = FastOutLinearIn
        )
    
    // Hover effect animation - For interactive elements
    val HoverEffect: FloatAnimationSpec<Float> = 
        infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    
    // Loading spinner animation
    val LoadingSpinner: AnimationSpec<Float> = infiniteRepeatable(
        animation = tween(800, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
    
    // Success celebration animation
    val SuccessCelebration: AnimationSpec<Float> = repeatable(
        iterations = 3,
        animation = tween(300, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
    )
    
    // Error shake animation
    val ErrorShake: AnimationSpec<Float> = infiniteRepeatable(
        animation = tween(100, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
    
    // Custom transition for shared elements
    fun sharedElementTransition() = transitionDefinition<Float> {
        // Define custom shared element transitions
    }
    
    // Animation presets for common use cases
    object Presets {
        val ButtonPress = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
        
        val CardFlip = tween(600, easing = FastOutSlowInEasing)
        
        val ModalEnter = slideIn(
            { fullSize -> fullSize },
            animationSpec = FastOutSlowIn
        )
        
        val ModalExit = slideOut(
            { fullSize -> -fullSize },
            animationSpec = FastOutLinearIn
        )
    }
}