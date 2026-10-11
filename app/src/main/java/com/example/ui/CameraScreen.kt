package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.camera.CameraController
import com.example.camera.CameraPermissionView
import com.example.camera.CameraPreview
import com.example.ui.components.ScallopShape
import com.example.ui.theme.AndroidifyBlack
import com.example.ui.theme.AndroidifyGreen

@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoCaptured(uri)
        }
    }

    var isFrontCamera by remember { mutableStateOf(false) }
    var isTorchOn by remember { mutableStateOf(false) }
    var zoomIndex by remember { mutableIntStateOf(0) } // 0 = 1X, 1 = 2X

    val controller = remember {
        CameraController(
            context = context,
            onPhotoCaptured = { uri -> onPhotoCaptured(uri) },
            onError = { /* fallback in controller */ }
        )
    }

    if (!hasCameraPermission) {
        CameraPermissionView(
            onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onPickFromGallery = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF262A27))
    ) {
        // Full Viewfinder Preview
        CameraPreview(
            isFrontCamera = isFrontCamera,
            isTorchOn = isTorchOn,
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )

        // Top Back and Torch Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.95f), CircleShape)
                    .clickable { onBack() }
                    .testTag("camera_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AndroidifyBlack,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Torch Toggle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.95f), CircleShape)
                    .clickable { isTorchOn = !isTorchOn }
                    .testTag("camera_torch_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Torch",
                    tint = if (isTorchOn) AndroidifyGreen else AndroidifyBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Central Area with Guide Frame (Matching Screenshot 3)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 170.dp, start = 20.dp, end = 20.dp)
                .drawBehind {
                    val stroke = Stroke(
                        width = 3.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f), 0f)
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.95f),
                        cornerRadius = CornerRadius(36.dp.toPx(), 36.dp.toPx()),
                        style = stroke
                    )
                }
        ) {
            // Frame Guide Message Pill resting right at the bottom edge of the dashed frame (Matching Screenshot 3)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
                    .testTag("frame_guide_banner")
            ) {
                Text(
                    text = "Use guides to frame yourself for the perfect story",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = AndroidifyBlack,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp)
                )
            }
        }

        // Bottom Controls (Matching Screenshot 3)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1X / 2X Zoom Toggle Pill (Matching Screenshot 3)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("zoom_toggle_pill")
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1X Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (zoomIndex == 0) AndroidifyGreen else Color.Transparent)
                            .clickable { zoomIndex = 0 }
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1X",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (zoomIndex == 0) Color.White else AndroidifyBlack
                        )
                    }

                    // 2X Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (zoomIndex == 1) AndroidifyGreen else Color.Transparent)
                            .clickable { zoomIndex = 1 }
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2X",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (zoomIndex == 1) Color.White else AndroidifyBlack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shutter Button & Lens Switch Row (Matching Screenshot 3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                // Camera Flip Round Button on Bottom Left (Matching Screenshot 3)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(54.dp)
                        .background(Color.White, CircleShape)
                        .border(1.2.dp, Color(0xFFE0E5E0), CircleShape)
                        .clickable { isFrontCamera = !isFrontCamera }
                        .testTag("flip_camera_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Flip Camera",
                        tint = AndroidifyBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Dual-Layer Marshmallow Scalloped Shutter Button (Matching Screenshot 3)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "shutter")

                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .scale(scale)
                        .clip(ScallopShape(lobes = 10, depthRatio = 0.065f))
                        .background(Color.White.copy(alpha = 0.4f)) // Outer wavy halo
                        .border(3.dp, Color.White, ScallopShape(lobes = 10, depthRatio = 0.065f))
                        .padding(5.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            controller.takePicture()
                        }
                        .testTag("camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner solid scalloped button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(ScallopShape(lobes = 10, depthRatio = 0.065f))
                            .background(Color.White)
                    )
                }

                // Gallery Button on Bottom Right
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(54.dp)
                        .background(Color.White, CircleShape)
                        .border(1.2.dp, Color(0xFFE0E5E0), CircleShape)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("camera_gallery_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = AndroidifyBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
