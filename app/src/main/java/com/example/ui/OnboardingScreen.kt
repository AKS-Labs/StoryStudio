package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AndroidifyHeader
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyBlue
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifyPaleMint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onLetsGo: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Animated Brush Drawing Progress (0f -> 1f) for real-time doodled 'S' paint reveal
    val brushDrawProgress = remember { Animatable(0f) }

    // 2. Kinetic Typography Motion Graphics Staggered States
    val word1Scale = remember { Animatable(0.4f) }
    val word1Alpha = remember { Animatable(0f) }
    val word1Rotation = remember { Animatable(-6f) }
    val word1Y = remember { Animatable(50f) }

    val word2Scale = remember { Animatable(0.4f) }
    val word2Alpha = remember { Animatable(0f) }
    val word2Rotation = remember { Animatable(6f) }
    val word2Y = remember { Animatable(50f) }

    val word3Scale = remember { Animatable(0.3f) }
    val word3Alpha = remember { Animatable(0f) }
    val word3Rotation = remember { Animatable(-3f) }
    val word3Y = remember { Animatable(60f) }

    val storyBadgeScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Kick off real-time animated brush stroke painting the doodled 'S'
        launch {
            brushDrawProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
            )
        }

        // Staggered Motion Graphics Word 1: "CUSTOMIZE"
        delay(300)
        launch {
            word1Alpha.animateTo(1f, tween(300))
        }
        launch {
            word1Y.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        }
        launch {
            word1Rotation.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        word1Scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        // Staggered Motion Graphics Word 2: "YOUR OWN"
        delay(180)
        launch {
            word2Alpha.animateTo(1f, tween(300))
        }
        launch {
            word2Y.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        }
        launch {
            word2Rotation.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        word2Scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        // Staggered Motion Graphics Word 3: "STORY"
        delay(220)
        launch {
            word3Alpha.animateTo(1f, tween(300))
        }
        launch {
            word3Y.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        }
        launch {
            word3Rotation.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        launch {
            storyBadgeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium)
            )
        }
        word3Scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    // Continuous subtle kinetic floating rhythm
    val infiniteTransition = rememberInfiniteTransition(label = "kinetic_typography_float")
    val float1 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse),
        label = "w1_float"
    )
    val float2 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Reverse),
        label = "w2_float"
    )
    val float3 by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "w3_float"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AndroidifyPaleMint)
    ) {
        // Doodled style 'S' painted in real time across the screen by an animated big brush
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sweeping hand-drawn cursive/expressive 'S' brush doodle path
            val fullSPath = Path().apply {
                // Top loop of the 'S'
                moveTo(w * 0.88f, h * 0.16f)
                cubicTo(w * 0.75f, h * 0.08f, w * 0.22f, h * 0.10f, w * 0.14f, h * 0.28f)
                cubicTo(w * 0.08f, h * 0.42f, w * 0.38f, h * 0.48f, w * 0.72f, h * 0.54f)
                // Bottom loop of the 'S'
                cubicTo(w * 0.95f, h * 0.60f, w * 0.94f, h * 0.78f, w * 0.68f, h * 0.86f)
                cubicTo(w * 0.42f, h * 0.92f, w * 0.12f, h * 0.85f, w * 0.20f, h * 0.95f)
            }

            val pathMeasure = PathMeasure()
            pathMeasure.setPath(fullSPath, false)
            val pathLen = pathMeasure.length
            val animatedSegment = Path()

            val currentProgress = brushDrawProgress.value.coerceIn(0f, 1f)
            if (currentProgress > 0f && pathLen > 0f) {
                pathMeasure.getSegment(0f, pathLen * currentProgress, animatedSegment, true)

                // Main broad wet-paint brush stroke
                drawPath(
                    path = animatedSegment,
                    color = AndroidifyGreen,
                    style = Stroke(
                        width = 170.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Inner saturation core stroke giving authentic brush bristle depth
                drawPath(
                    path = animatedSegment,
                    color = Color(0xFF4EE892),
                    style = Stroke(
                        width = 90.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Decorative painterly wet-paint droplets and accents once drawing advances
            if (currentProgress > 0.4f) {
                drawCircle(Color(0xFF34C876), radius = 10.dp.toPx(), center = Offset(w * 0.90f, h * 0.44f))
            }
            if (currentProgress > 0.7f) {
                drawCircle(Color(0xFF34C876), radius = 12.dp.toPx(), center = Offset(w * 0.12f, h * 0.72f))
            }

            // Playful lime doodle accent in top-right
            val limeColor = Color(0xFFD6F528)
            drawArc(
                color = limeColor,
                startAngle = 185f,
                sweepAngle = 105f,
                useCenter = false,
                topLeft = Offset(w - 110.dp.toPx(), 70.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(74.dp.toPx(), 74.dp.toPx()),
                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Playful white brush dashes in bottom-left
            val whiteColor = Color.White.copy(alpha = 0.95f)
            val dashW = 9.dp.toPx()
            drawLine(whiteColor, Offset(w * 0.08f, h * 0.84f), Offset(w * 0.14f, h * 0.80f), strokeWidth = dashW, cap = StrokeCap.Round)
            drawLine(whiteColor, Offset(w * 0.12f, h * 0.87f), Offset(w * 0.18f, h * 0.83f), strokeWidth = dashW, cap = StrokeCap.Round)
            drawLine(whiteColor, Offset(w * 0.16f, h * 0.90f), Offset(w * 0.22f, h * 0.86f), strokeWidth = dashW, cap = StrokeCap.Round)
        }

        // Top App Bar Pill with StoryStudio Title
        AndroidifyHeader(
            title = "StoryStudio",
            onBack = null,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Center Kinetic Typography Motion Graphics (No cursor, high impact, ultra big display)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.testTag("animated_welcome_text")
            ) {
                // Word 1: CUSTOMIZE
                Text(
                    text = "CUSTOMIZE",
                    fontSize = 58.sp,
                    lineHeight = 62.sp,
                    fontWeight = FontWeight.Black,
                    color = AndroidifyBlack,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-2.2).sp,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = word1Scale.value
                            scaleY = word1Scale.value
                            alpha = word1Alpha.value
                            translationY = word1Y.value + float1
                            rotationZ = word1Rotation.value
                        }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Word 2: YOUR OWN
                Text(
                    text = "YOUR OWN",
                    fontSize = 52.sp,
                    lineHeight = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = AndroidifyBlack,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-1.8).sp,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = word2Scale.value
                            scaleY = word2Scale.value
                            alpha = word2Alpha.value
                            translationY = word2Y.value + float2
                            rotationZ = word2Rotation.value
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Word 3: STORY with kinetic pop badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = word3Scale.value
                            scaleY = word3Scale.value
                            alpha = word3Alpha.value
                            translationY = word3Y.value + float3
                            rotationZ = word3Rotation.value
                        }
                ) {
                    Text(
                        text = "STORY",
                        fontSize = 72.sp,
                        lineHeight = 74.sp,
                        fontWeight = FontWeight.Black,
                        color = AndroidifyBlack,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-2.8).sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Motion Graphics Kinetic Sparkle Pill Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AndroidifyBlue,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = storyBadgeScale.value
                                scaleY = storyBadgeScale.value
                            }
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Bottom Controls Container: Pagination Indicator & Blue "Let's Go" Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 36.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pagination Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 8.dp)
                        .background(AndroidifyBlack, RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .size(width = 8.dp, height = 8.dp)
                        .background(Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Blue "Let's Go" Button
            Button(
                onClick = onLetsGo,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
                    .testTag("onboarding_lets_go_button"),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AndroidifyBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "Let's Go",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
