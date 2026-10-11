package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AndroidifyHeader
import com.example.ui.components.ScallopShape
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen
import com.example.ui.theme.AndroidifyPaleMint
import com.example.ui.theme.AndroidifySurface

@Composable
fun HubScreen(
    onLaunchCamera: () -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onStartBlankCanvas: () -> Unit,
    onBackToOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSegment by remember { mutableIntStateOf(0) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AndroidifyPaleMint)
    ) {
        // Bottom Organic Green Wave (Matching Screenshot 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
                .background(AndroidifyGreen)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar Pill with StorStudio Title (Matching Screenshot 2)
            AndroidifyHeader(
                title = "StorStudio",
                onBack = onBackToOnboarding
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Pill Control: Photo vs Canvas (Matching Screenshot 2)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AndroidifyBlack),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .testTag("segmented_mode_selector")
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Photo Option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedSegment == 0) AndroidifyBlack else Color.Transparent)
                            .clickable { selectedSegment = 0 }
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Photo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedSegment == 0) Color.White else AndroidifyBlack
                        )
                    }

                    // Canvas / Prompt Option
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedSegment == 1) AndroidifyBlack else Color.Transparent)
                            .clickable { selectedSegment = 1 }
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Canvas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (selectedSegment == 1) Color.White else AndroidifyBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Large Dashed Rounded Rectangle Frame (Matching Screenshot 2)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .drawBehind {
                        val stroke = Stroke(
                            width = 3.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f), 0f)
                        )
                        drawRoundRect(
                            color = AndroidifyBlack,
                            cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx()),
                            style = stroke
                        )
                    }
                    .clip(RoundedCornerShape(32.dp))
                    .background(AndroidifySurface)
                    .clickable {
                        if (selectedSegment == 0) onLaunchCamera() else onStartBlankCanvas()
                    }
                    .testTag("hub_camera_card"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Soft Rounded Marshmallow Scallop Camera Badge (Matching Screenshot 2)
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(ScallopShape(lobes = 10, depthRatio = 0.065f))
                            .background(AndroidifyBlack)
                            .clickable {
                                if (selectedSegment == 0) onLaunchCamera() else onStartBlankCanvas()
                            }
                            .testTag("scallop_camera_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture Photo",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = if (selectedSegment == 0) "Take or\nchoose a photo" else "Start clean\nStatus canvas",
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = AndroidifyBlack,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // "Choose photo" Pill Button (Matching Screenshot 2)
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("choose_photo_pill_button"),
                        shape = RoundedCornerShape(25.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AndroidifyBlack),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = AndroidifyPaleMint,
                            contentColor = AndroidifyBlack
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Choose photo",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action Row (Matching Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pill Button: "Status Stickers"
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AndroidifyBlack),
                    modifier = Modifier
                        .height(54.dp)
                        .clickable { onStartBlankCanvas() }
                        .testTag("quick_stickers_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(AndroidifyGreen, CircleShape)
                                .border(1.dp, AndroidifyBlack, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Status Stickers",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AndroidifyBlack
                        )
                    }
                }

                // Right Black Pill Button: "Create Story ->"
                Button(
                    onClick = {
                        if (selectedSegment == 0) onLaunchCamera() else onStartBlankCanvas()
                    },
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("create_story_pill_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AndroidifyBlack,
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Create Story",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
