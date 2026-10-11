package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AndroidifyHeader
import com.example.ui.components.BrushDoodleShape
import com.example.ui.components.ScallopShape
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyBlue
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifyPaleMint

@Composable
fun OnboardingScreen(
    onLetsGo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AndroidifyPaleMint)
    ) {
        // Hand-painted organic green brush doodle blob (Matching Screenshot 1)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 90.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
                .clip(BrushDoodleShape())
                .background(AndroidifyGreen)
        )

        // Cute lime doodle brush ripple in top-right (Matching Screenshot 1)
        Canvas(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 28.dp)
        ) {
            val limeColor = Color(0xFFD6F528)
            // 3 hand-painted wavy brush strokes
            drawArc(
                color = limeColor,
                startAngle = 200f,
                sweepAngle = 100f,
                useCenter = false,
                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = limeColor,
                startAngle = 210f,
                sweepAngle = 80f,
                useCenter = false,
                topLeft = Offset(10f, 16f),
                size = size.copy(width = size.width - 20f, height = size.height - 20f),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Cute white doodle brush marks in bottom-left (Matching Screenshot 1)
        Canvas(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomStart)
                .padding(bottom = 120.dp, start = 32.dp)
        ) {
            val whiteColor = Color.White.copy(alpha = 0.9f)
            drawLine(whiteColor, Offset(10f, 40f), Offset(22f, 24f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
            drawLine(whiteColor, Offset(24f, 48f), Offset(36f, 32f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
            drawLine(whiteColor, Offset(38f, 56f), Offset(50f, 40f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
        }

        // Top App Bar Pill with StorStudio Title (Matching Screenshot 1)
        AndroidifyHeader(
            title = "StorStudio",
            onBack = null,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Center Content Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 36.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Big Bold Display Title (Matching Screenshot 1)
            Text(
                text = "Customize\nyour own\nStatus story",
                fontSize = 42.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AndroidifyBlack,
                textAlign = TextAlign.Center,
                letterSpacing = (-1).sp,
                modifier = Modifier.padding(top = 28.dp)
            )

            // Center Character / Emblem (Matching Screenshot 1)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(ScallopShape(lobes = 10, depthRatio = 0.065f))
                    .background(Color.White)
                    .border(2.dp, AndroidifyBlack, ScallopShape(lobes = 10, depthRatio = 0.065f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤖", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(AndroidifyGreen, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STATUS BOT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AndroidifyBlack
                        )
                    }
                }
            }

            // Bottom Pagination Indicator & Blue "Let's Go" Button (Matching Screenshot 1)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
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

                // Blue "Let's Go" Button (Matching Screenshot 1)
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
}
